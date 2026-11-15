package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20150915Test {
    private val protocol = Protocols.select(20150915)

    @Test
    fun `protocol negotiation defaults to version nineteen`() {
        protocol.assertPacket(
            "4b00000400000013000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }
}
