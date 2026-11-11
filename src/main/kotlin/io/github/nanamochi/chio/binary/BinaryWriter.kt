package io.github.nanamochi.chio.binary

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.writeDoubleLe
import kotlinx.io.writeFloatLe
import kotlinx.io.writeIntLe
import kotlinx.io.writeLongLe
import kotlinx.io.writeShortLe
import kotlinx.io.writeUByte
import kotlinx.io.writeUIntLe
import kotlinx.io.writeULongLe
import kotlinx.io.writeUShortLe

/** Writes a packet payload as little-endian values. */
@JvmInline
value class BinaryWriter(val buffer: Buffer = Buffer()) {
    fun writeUByte(value: Int) {
        buffer.writeUByte(value.toUByte())
    }

    fun writeByte(value: Int) {
        buffer.writeByte(value.toByte())
    }

    fun writeUShort(value: Int) {
        buffer.writeUShortLe(value.toUShort())
    }

    fun writeShort(value: Int) {
        buffer.writeShortLe(value.toShort())
    }

    fun writeUInt(value: Long) {
        buffer.writeUIntLe(value.toUInt())
    }

    fun writeUInt(value: Int) {
        buffer.writeUIntLe(value.toUInt())
    }

    fun writeInt(value: Int) {
        buffer.writeIntLe(value)
    }

    fun writeULong(value: Long) {
        buffer.writeULongLe(value.toULong())
    }

    fun writeLong(value: Long) {
        buffer.writeLongLe(value)
    }

    fun writeFloat(value: Float) {
        buffer.writeFloatLe(value)
    }

    fun writeDouble(value: Double) {
        buffer.writeDoubleLe(value)
    }

    fun writeBoolean(value: Boolean) {
        writeUByte(if (value) 1 else 0)
    }

    fun writeUleb128(value: Int) {
        var remaining = value.toUInt()
        do {
            var byte = (remaining and 0x7Fu).toInt()
            remaining = remaining shr 7
            if (remaining != 0u) byte = byte or 0x80
            writeUByte(byte)
        } while (remaining != 0u)
    }

    fun writeString(value: String) {
        if (value.isEmpty()) {
            writeUByte(0)
            return
        }
        val bytes = value.encodeToByteArray()
        writeUByte(0x0B)
        writeUleb128(bytes.size)
        buffer.write(bytes)
    }

    fun writeBooleanList(values: List<Boolean>) {
        var packed = 0
        values.forEachIndexed { index, value -> if (value) packed = packed or (1 shl index) }
        writeUByte(packed)
    }

    fun writeIntList16(values: IntArray) {
        writeUShort(values.size)
        values.forEach(::writeInt)
    }

    fun writeIntList32(values: IntArray) {
        writeUInt(values.size)
        values.forEach(::writeInt)
    }

    fun writeBytes(value: ByteArray) {
        buffer.write(value)
    }

    fun toByteArray(): ByteArray = buffer.readByteArray()

    val size: Long
        get() = buffer.size
}
