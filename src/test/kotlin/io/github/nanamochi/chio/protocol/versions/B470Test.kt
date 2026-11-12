package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
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

class B470Test {
    private val protocol = Protocols.select(470)

    @Test
    fun `match start includes mode`() {
        protocol.assertPacket(
            "2f00003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003",
            protocol.encode(MatchStartPacket(inboundMatchV470())),
        )
    }

    @Test
    fun `match update includes mode`() {
        protocol.assertPacket(
            "1b00003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003",
            protocol.encode(MatchUpdatePacket(inboundMatchV470())),
        )
    }

    @Test
    fun `match new includes mode`() {
        protocol.assertPacket(
            "1c00003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003",
            protocol.encode(MatchNewPacket(inboundMatchV470())),
        )
    }

    @Test
    fun `match join success includes mode`() {
        protocol.assertPacket(
            "2500003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV470())),
        )
    }

    @Test
    fun `read match create`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV470()),
            protocol.decode(
                hex(
                    "2000003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003"
                )
            ),
        )
    }

    @Test
    fun `read match change settings`() {
        assertEquals(
            MatchChangeSettingsPacket(inboundMatchV470()),
            protocol.decode(
                hex(
                    "2a00003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003"
                )
            ),
        )
    }

    @Test
    fun `match update and new skip ids above one byte`() {
        val match = inboundMatchV470().copy(id = 256)

        assertEquals(0, protocol.encode(MatchUpdatePacket(match)).size)
        assertEquals(0, protocol.encode(MatchNewPacket(match)).size)
    }

    @Test
    fun `b452 doesn't include mode`() {
        val older = Protocols.select(452)

        older.assertPacket(
            "1c00003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000",
            older.encode(MatchNewPacket(inboundMatchV470())),
        )
    }
}

private fun inboundMatchV470(): Match =
    Match(
            id = 3,
            inProgress = true,
            type = MatchType.Standard,
            mods = Mods.HardRock + Mods.Hidden,
            name = "Test match",
            beatmapText = "Test - Test",
            beatmapId = 0,
            beatmapChecksum = "",
            slots =
                listOf(
                    MatchSlot(-1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(2, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(3, SlotStatus.Ready, SlotTeam.Neutral, Mods.NoMod),
                ),
            hostId = 2,
            mode = Mode.OsuMania,
            scoringType = ScoringType.Score,
            teamType = TeamType.HeadToHead,
            freemod = false,
            seed = 0,
        )
        .withMinimumSlotCount(8)
