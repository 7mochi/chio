package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.DEFAULT_MAX_PACKET_SIZE
import io.github.nanamochi.chio.PacketTooLargeException
import io.github.nanamochi.chio.compression.Gzip
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readUByte
import kotlinx.io.readUIntLe
import kotlinx.io.writeIntLe
import kotlinx.io.writeUByte
import kotlinx.io.writeUShortLe

/**
 * Packet header and compression.
 *
 * Payload codecs only deal with packet fields. This interface owns the surrounding bytes.
 */
interface PacketFormat {
    val headerSize: Int

    /** Reads one packet payload with [DEFAULT_MAX_PACKET_SIZE]. */
    fun read(source: Source): ByteArray = read(source, DEFAULT_MAX_PACKET_SIZE)

    /**
     * Reads one packet payload.
     *
     * The declared and decompressed sizes must stay below [maxSize]. [Protocol] supplies the
     * packet-specific limit.
     */
    fun read(source: Source, maxSize: Int): ByteArray

    fun write(
        sink: Sink,
        packetId: Int,
        payload: ByteArray,
        disableCompression: Boolean,
        includeMTime: Boolean,
    )
}

/**
 * Versions 282–323: `u16 id`, `u32 compressed length`, gzip payload. Every payload is compressed.
 */
object LegacyPacketFormat : PacketFormat {
    override val headerSize: Int = 6

    override fun read(source: Source, maxSize: Int): ByteArray {
        val length = source.readUIntLe().toLong()
        if (length == 0L) return ByteArray(0)
        if (length >= maxSize) throw PacketTooLargeException(length, maxSize)
        return Gzip.decompress(source.readByteArray(length.toInt()), maxSize)
    }

    override fun write(
        sink: Sink,
        packetId: Int,
        payload: ByteArray,
        disableCompression: Boolean,
        includeMTime: Boolean,
    ) {
        val body = Gzip.compress(payload, includeMTime)
        sink.writeUShortLe(packetId.toUShort())
        sink.writeIntLe(body.size)
        sink.write(body)
    }
}

/**
 * Versions 334+: `u16 id`, `u8 compressed flag`, `u32 length`, payload.
 *
 * Payloads above 150 bytes are gzip-compressed. Smaller ones stay raw, matching what clients
 * expect.
 */
class CompressionFlagPacketFormat @JvmOverloads constructor(private val threshold: Int = 150) :
    PacketFormat {
    override val headerSize: Int = 7

    override fun read(source: Source, maxSize: Int): ByteArray {
        val compressed = source.readUByte().toInt() != 0
        val length = source.readUIntLe().toLong()
        if (length == 0L) return ByteArray(0)
        if (length >= maxSize) throw PacketTooLargeException(length, maxSize)
        val body = source.readByteArray(length.toInt())
        return if (compressed) Gzip.decompress(body, maxSize) else body
    }

    override fun write(
        sink: Sink,
        packetId: Int,
        payload: ByteArray,
        disableCompression: Boolean,
        includeMTime: Boolean,
    ) {
        val compressed = payload.size > threshold && !disableCompression
        val body = if (compressed) Gzip.compress(payload, includeMTime) else payload

        sink.writeUShortLe(packetId.toUShort())
        sink.writeUByte(if (compressed) 1u else 0u)
        sink.writeIntLe(body.size)
        sink.write(body)
    }
}
