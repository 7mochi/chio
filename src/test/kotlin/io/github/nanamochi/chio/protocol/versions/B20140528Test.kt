package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20140528Test {
    private val protocol = Protocols.select(20140528)

    @Test
    fun `protocol negotiation defaults to version eighteen`() {
        protocol.assertPacket(
            "4b00000400000012000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }
}
