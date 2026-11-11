package io.github.nanamochi.chio.protocol

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.io.Buffer
import kotlinx.io.readUShortLe

fun hex(value: String): ByteArray {
    require(value.length % 2 == 0) { "Hex string has an odd length" }

    return ByteArray(value.length / 2) { index ->
        value.substring(index * 2, index * 2 + 2).toInt(16).toByte()
    }
}

fun Protocol.assertPacket(expectedHex: String, actual: ByteArray) {
    val expectedBuffer = Buffer().apply { write(hex(expectedHex)) }
    val actualBuffer = Buffer().apply { write(actual) }

    while (expectedBuffer.size > 0 || actualBuffer.size > 0) {
        assertEquals(expectedBuffer.size > 0, actualBuffer.size > 0, "frame count mismatch")
        assertEquals(expectedBuffer.readUShortLe(), actualBuffer.readUShortLe(), "packet id")

        val expectedPayload = format.read(expectedBuffer)
        val actualPayload = format.read(actualBuffer)

        assertContentEquals(expectedPayload, actualPayload)
    }
}
