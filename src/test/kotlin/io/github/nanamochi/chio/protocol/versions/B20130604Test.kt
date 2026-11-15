package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.SwitchServerPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130604Test {
    private val protocol = Protocols.select(20130604)

    @Test
    fun `switch server writes the idle time`() {
        protocol.assertPacket("67000004000000100e0000", protocol.encode(SwitchServerPacket(3600)))
    }
}
