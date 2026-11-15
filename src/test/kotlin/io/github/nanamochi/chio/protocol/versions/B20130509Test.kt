package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.VersionUpdateForcedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130509Test {
    private val protocol = Protocols.select(20130509)

    @Test
    fun `forced version update is empty`() {
        protocol.assertPacket("66000000000000", protocol.encode(VersionUpdateForcedPacket))
    }
}
