package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130118Test {
    private val protocol = Protocols.select(20130118)

    @Test
    fun `match writes freemod`() {
        protocol.assertPacket(
            "2e00004600000000010100000000000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000020202020202020200000000000000000200000000000001",
            protocol.encode(MatchStartPacket(match())),
        )
    }
}

private fun match(): Match =
    Match(
            id = 256,
            inProgress = true,
            name = "Test match",
            password = "secret",
            beatmapText = "Test - Test",
            beatmapId = 0,
            hostId = 2,
            freemod = true,
        )
        .withMinimumSlotCount(8)
