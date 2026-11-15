package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20130418Test {
    private val protocol = Protocols.select(20130418)

    @Test
    fun `match writes a seed`() {
        protocol.assertPacket(
            "2e00004a00000000010100000000000b0a54657374206d617463680b067365637265740b0b54657374202d2054657374000000000002020202020202020000000000000000020000000000000040e20100",
            protocol.encode(MatchStartPacket(match())),
        )
    }

    @Test
    fun `spectate frames write the extra value`() {
        protocol.assertPacket(
            "0f0000070000002a000000000000",
            protocol.encode(SpectateFramesPacket(bundle())),
        )
    }

    @Test
    fun `read spectate frames reads the extra value`() {
        assertEquals(
            InboundSpectateFramesPacket(bundle()),
            protocol.decode(hex("120000070000002a000000000000")),
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
            seed = 123456,
        )
        .withMinimumSlotCount(8)

private fun bundle(): ReplayFrameBundle =
    ReplayFrameBundle(action = ReplayAction.Standard, extra = 42)
