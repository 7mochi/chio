package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchJoinPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchJoin
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

class B591Test {
    private val protocol = Protocols.select(591)

    @Test
    fun `match start includes password`() {
        protocol.assertPacket(
            "2f00004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchStartPacket(inboundMatchV591())),
        )
    }

    @Test
    fun `match update includes password`() {
        protocol.assertPacket(
            "1b00004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchUpdatePacket(inboundMatchV591())),
        )
    }

    @Test
    fun `match new includes password`() {
        protocol.assertPacket(
            "1c00004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchNewPacket(inboundMatchV591())),
        )
    }

    @Test
    fun `match join success includes password`() {
        protocol.assertPacket(
            "2500004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV591())),
        )
    }

    @Test
    fun `read match create includes password`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV591()),
            protocol.decode(
                hex(
                    "2000004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read match change settings includes password`() {
        assertEquals(
            MatchChangeSettingsPacket(inboundMatchV591()),
            protocol.decode(
                hex(
                    "2a00004a00000003010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read match join includes password`() {
        assertEquals(
            MatchJoinPacket(MatchJoin(matchId = 3, password = "secret")),
            protocol.decode(hex("2100000c000000030000000b06736563726574")),
        )
    }

    @Test
    fun `match update and new skip ids above one byte`() {
        val match = inboundMatchV591().copy(id = 256)

        assertEquals(0, protocol.encode(MatchUpdatePacket(match)).size)
        assertEquals(0, protocol.encode(MatchNewPacket(match)).size)
    }

    @Test
    fun `b558 doesn't include match password`() {
        val older = Protocols.select(558)

        older.assertPacket(
            "2f00004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            older.encode(MatchStartPacket(inboundMatchV591())),
        )
    }

    @Test
    fun `b558 ignores match join password`() {
        val older = Protocols.select(558)

        assertEquals(
            MatchJoinPacket(MatchJoin(matchId = 3)),
            older.decode(hex("2100000c000000030000000b06736563726574")),
        )
    }
}

private fun inboundMatchV591(): Match =
    Match(
            id = 3,
            inProgress = true,
            type = MatchType.Standard,
            mods = Mods.HardRock + Mods.Hidden,
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
            mode = Mode.OsuMania,
            scoringType = ScoringType.ScoreV2,
            teamType = TeamType.TagTeamVs,
            freemod = false,
            seed = 0,
        )
        .withMinimumSlotCount(8)
