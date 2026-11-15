package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserSilencedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20121212Test {
    private val protocol = Protocols.select(20121212)

    @Test
    fun `user silenced writes a user id`() {
        protocol.assertPacket("5e00000400000007000000", protocol.encode(UserSilencedPacket(7)))
    }
}
