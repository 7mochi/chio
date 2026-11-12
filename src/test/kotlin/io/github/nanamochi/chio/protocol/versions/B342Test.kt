package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.MatchSkipRequestPacket
import io.github.nanamochi.chio.packet.outbound.MatchSkipPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B342Test {
    private val protocol = Protocols.select(342)

    @Test
    fun `match skip uses b342 ids`() {
        protocol.assertPacket("3e000000000000", protocol.encode(MatchSkipPacket))
    }

    @Test
    fun `read match skip request`() {
        assertEquals(MatchSkipRequestPacket, protocol.decode(hex("3d000000000000")))
    }

    @Test
    fun `b340 doesn't support match skip`() {
        val older = Protocols.select(340)

        assertFailsWith<UnsupportedPacketException> { older.encode(MatchSkipPacket) }
        assertFailsWith<UnsupportedPacketException> { older.decode(hex("3d000000000000")) }
    }
}
