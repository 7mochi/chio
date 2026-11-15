package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.RTXPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20140731Test {
    private val protocol = Protocols.select(20140731)

    @Test
    fun `rtx sends the default message`() {
        protocol.assertPacket(
            "6900001b0000000b195a616c6c69757327206579657320686176652061776f6b656e",
            protocol.encode(RTXPacket()),
        )
    }
}
