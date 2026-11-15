package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchChangePasswordPacket as InboundMatchChangePasswordPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.outbound.MatchChangePasswordPacket
import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.ScoringType
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam
import io.github.nanamochi.chio.packet.types.TeamType
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20120703Test {
    private val protocol = Protocols.select(20120703)

    @Test
    fun `match change password writes a password`() {
        protocol.assertPacket(
            "5b0000080000000b06736563726574",
            protocol.encode(MatchChangePasswordPacket("secret")),
        )
    }

    @Test
    fun `read match change password reads a match`() {
        assertEquals(
            InboundMatchChangePasswordPacket(match()),
            protocol.decode(
                hex(
                    "5a00004b0000000001010000000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000000000"
                )
            ),
        )
    }
}

private fun match(): Match =
    Match(
            id = 256,
            inProgress = true,
            type = MatchType.Standard,
            mods = Mods.NoMod,
            name = "Test match",
            password = "secret",
            beatmapText = "Test - Test",
            beatmapId = 0,
            beatmapChecksum = "",
            slots =
                listOf(
                    MatchSlot(-1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(2, SlotStatus.HasPlayer, SlotTeam.Blue, Mods.NoMod),
                    MatchSlot(3, SlotStatus.Ready, SlotTeam.Red, Mods.NoMod),
                ),
            hostId = 2,
            mode = Mode.Osu,
            scoringType = ScoringType.Score,
            teamType = TeamType.HeadToHead,
        )
        .withMinimumSlotCount(8)
