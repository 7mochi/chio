package io.github.nanamochi.chio.binary

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readDoubleLe
import kotlinx.io.readFloatLe
import kotlinx.io.readIntLe
import kotlinx.io.readLongLe
import kotlinx.io.readShortLe
import kotlinx.io.readString
import kotlinx.io.readUByte
import kotlinx.io.readUIntLe
import kotlinx.io.readULongLe
import kotlinx.io.readUShortLe

/** Reads a packet payload as little-endian values. */
@JvmInline
value class BinaryReader(val buffer: Buffer) {
    fun readUByte(): Int = buffer.readUByte().toInt()

    fun readByte(): Byte = buffer.readByte()

    fun readUShort(): Int = buffer.readUShortLe().toInt()

    fun readShort(): Short = buffer.readShortLe()

    fun readUInt(): Long = buffer.readUIntLe().toLong()

    fun readInt(): Int = buffer.readIntLe()

    fun readULong(): Long = buffer.readULongLe().toLong()

    fun readLong(): Long = buffer.readLongLe()

    fun readFloat(): Float = buffer.readFloatLe()

    fun readDouble(): Double = buffer.readDoubleLe()

    fun readBoolean(): Boolean = buffer.readUByte().toInt() != 0

    fun readUleb128(): Int {
        var result = 0
        var shift = 0
        while (true) {
            val byte = readUByte()
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0) return result
            shift += 7
        }
    }

    fun readString(): String {
        if (readUByte() == 0) return ""
        val length = readUleb128()
        if (length == 0) return ""
        return buffer.readString(length.toLong())
    }

    fun readBooleanList(size: Int): List<Boolean> {
        val packed = readUByte()
        return List(size) { index -> (packed ushr index) and 1 != 0 }
    }

    fun readIntList16(): IntArray {
        val count = readUShort()
        return IntArray(count) { readInt() }
    }

    fun readIntList32(): IntArray {
        val count = readUInt()
        require(count <= Int.MAX_VALUE) { "List length $count is too large" }
        return IntArray(count.toInt()) { readInt() }
    }

    fun readBytes(count: Int): ByteArray = buffer.readByteArray(count)

    val remaining: Long
        get() = buffer.size

    fun hasRemaining(): Boolean = !buffer.exhausted()
}
