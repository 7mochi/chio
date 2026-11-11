package io.github.nanamochi.chio.compression

import io.github.nanamochi.chio.DEFAULT_MAX_PACKET_SIZE
import io.github.nanamochi.chio.PacketTooLargeException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.CRC32
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream

/**
 * Gzip wrapper used by Bancho packets.
 *
 * This class writes the header explicitly so the output stays the same regardless of the JDK's gzip
 * defaults:
 *
 * `1f 8b 08 00 <mtime:4> 02 ff`
 *
 * [Deflater] runs in raw mode because this class adds the header and trailer.
 */
object Gzip {
    private const val LEVEL = Deflater.BEST_COMPRESSION

    /**
     * Compresses [data] with a fixed gzip header.
     *
     * By default, the timestamp is zero, so the same input produces the same bytes. Set
     * [includeMTime] only when the current time is required.
     */
    @JvmStatic
    @JvmOverloads
    fun compress(data: ByteArray, includeMTime: Boolean = false): ByteArray {
        val out = ByteArrayOutputStream(data.size / 2 + 32)

        out.write(0x1F)
        out.write(0x8B)
        out.write(8)
        out.write(0)
        val mtime = if (includeMTime) (System.currentTimeMillis() / 1000L).toInt() else 0
        writeLe32(out, mtime.toLong() and 0xFFFF_FFFFL)
        out.write(2)
        out.write(0xFF)

        Deflater(LEVEL, true).use { deflater ->
            deflater.setInput(data)
            deflater.finish()
            val chunk = ByteArray(DEFAULT_BUFFER_SIZE)
            while (!deflater.finished()) {
                val count = deflater.deflate(chunk)
                out.write(chunk, 0, count)
            }
        }

        val crc = CRC32().apply { update(data) }.value
        writeLe32(out, crc)
        writeLe32(out, data.size.toLong() and 0xFFFF_FFFFL)

        return out.toByteArray()
    }

    /**
     * Decompresses [data], rejecting output larger than [maxSize].
     *
     * The limit stops a small compressed payload from expanding until memory runs out.
     */
    @JvmStatic
    @JvmOverloads
    fun decompress(data: ByteArray, maxSize: Int = DEFAULT_MAX_PACKET_SIZE): ByteArray {
        if (data.isEmpty()) return ByteArray(0)

        GZIPInputStream(ByteArrayInputStream(data)).use { input ->
            val out = ByteArrayOutputStream(minOf(maxSize, 8192))
            val chunk = ByteArray(DEFAULT_BUFFER_SIZE)
            var total = 0L

            while (true) {
                val count = input.read(chunk)
                if (count < 0) break
                total += count
                if (total > maxSize) throw PacketTooLargeException(total, maxSize)
                out.write(chunk, 0, count)
            }

            return out.toByteArray()
        }
    }

    private fun writeLe32(out: ByteArrayOutputStream, value: Long) {
        out.write((value and 0xFF).toInt())
        out.write(((value ushr 8) and 0xFF).toInt())
        out.write(((value ushr 16) and 0xFF).toInt())
        out.write(((value ushr 24) and 0xFF).toInt())
    }
}
