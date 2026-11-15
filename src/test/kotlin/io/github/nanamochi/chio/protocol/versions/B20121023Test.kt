package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.SilenceInfoPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20121023Test {
    private val protocol = Protocols.select(20121023)

    @Test
    fun `silence info writes a duration`() {
        protocol.assertPacket("5c000004000000100e0000", protocol.encode(SilenceInfoPacket(3600)))
    }
}
