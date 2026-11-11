package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MatchAllPlayersLoadedPacket
import io.github.nanamochi.chio.packet.outbound.MatchCompletePacket
import io.github.nanamochi.chio.packet.outbound.MatchPlayerFailedPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchTransferHostPacket
import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.ScoringType
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam
import io.github.nanamochi.chio.packet.types.TeamType
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B334Test {
    private val protocol = Protocols.select(334)

    @Test
    fun `match transfer host`() {
        protocol.assertPacket(
            "34000000000000",
            protocol.encode(MatchTransferHostPacket),
        )
    }

    @Test
    fun `match all players loaded`() {
        protocol.assertPacket(
            "37000000000000",
            protocol.encode(MatchAllPlayersLoadedPacket),
        )
    }

    @Test
    fun `match player failed`() {
        protocol.assertPacket(
            "3b00000400000003000000",
            protocol.encode(MatchPlayerFailedPacket(3)),
        )
    }

    @Test
    fun `match complete`() {
        protocol.assertPacket(
            "3c000000000000",
            protocol.encode(MatchCompletePacket),
        )
    }

    @Test
    fun `match start with match data`() {
        val match =
            Match(
                id = 1,
                inProgress = false,
                type = MatchType.Standard,
                mods = Mods.NoMod,
                name = "Test",
                password = "",
                beatmapText = "123",
                beatmapId = 0,
                beatmapChecksum = "",
                slots =
                    listOf(
                        MatchSlot(0, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                        MatchSlot(2, SlotStatus.NotReady, SlotTeam.Neutral, Mods.NoMod),
                        MatchSlot(3, SlotStatus.Ready, SlotTeam.Neutral, Mods.NoMod),
                    ),
                hostId = 1,
                mode = Mode.Osu,
                scoringType = ScoringType.Score,
                teamType = TeamType.HeadToHead,
                freemod = false,
                seed = 0,
            )
        protocol.assertPacket(
            "2f00002000000001000000000b04546573740b0331323300000000000104080200000003000000",
            protocol.encode(MatchStartPacket(match)),
        )
    }

    @Test
    fun `match score update without checksum`() {
        protocol.assertPacket(
            "3100001b0000000a00000000010000000000000000000000b80b0000010000000005",
            protocol.encode(
                MatchScoreUpdatePacket(ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5))
            ),
        )
    }
}
