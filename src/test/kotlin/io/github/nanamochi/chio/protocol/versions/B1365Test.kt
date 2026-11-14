package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.outbound.MatchPlayerSkippedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B1365Test {
    private val protocol = Protocols.select(1365)

    @Test
    fun `match player skipped writes slot id`() {
        protocol.assertPacket(
            "5200000400000003000000",
            protocol.encode(MatchPlayerSkippedPacket(slotId = 3)),
        )
    }

    @Test
    fun `match player skipped skips out of range slots`() {
        protocol.assertPacket("", protocol.encode(MatchPlayerSkippedPacket(slotId = 8)))
    }

    @Test
    fun `b1183 doesn't support match player skipped`() {
        val older = Protocols.select(1183)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(MatchPlayerSkippedPacket(slotId = 3))
        }
    }
}
