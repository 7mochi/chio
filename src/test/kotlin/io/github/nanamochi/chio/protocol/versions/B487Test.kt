package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B487Test {
    private val protocol = Protocols.select(487)

    @Test
    fun `protocol negotiation defaults to zero`() {
        protocol.assertPacket(
            "4c00000400000000000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `protocol negotiation accepts a custom version`() {
        protocol.assertPacket(
            "4c00000400000001000000",
            protocol.encode(ProtocolNegotiationPacket(version = 1)),
        )
    }

    @Test
    fun `b470 doesn't support protocol negotiation`() {
        val older = Protocols.select(470)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(ProtocolNegotiationPacket())
        }
    }
}
