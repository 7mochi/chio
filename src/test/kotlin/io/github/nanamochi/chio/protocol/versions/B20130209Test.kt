package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.TargetIsSilencedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130209Test {
    private val protocol = Protocols.select(20130209)

    @Test
    fun `target is silenced writes a username message`() {
        protocol.assertPacket(
            "6500001700000000000b0f746573745f757365726e616d655f32ffffffff",
            protocol.encode(TargetIsSilencedPacket("test_username_2")),
        )
    }
}
