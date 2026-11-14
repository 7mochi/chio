package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.packet.types.ScoringType
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam
import io.github.nanamochi.chio.packet.types.TeamType
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B535Test {
    private val protocol = Protocols.select(535)

    @Test
    fun `match start includes scoring and team`() {
        protocol.assertPacket(
            "2f00003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303",
            protocol.encode(MatchStartPacket(inboundMatchV535())),
        )
    }

    @Test
    fun `match update includes scoring and team`() {
        protocol.assertPacket(
            "1b00003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303",
            protocol.encode(MatchUpdatePacket(inboundMatchV535())),
        )
    }

    @Test
    fun `match new includes scoring and team`() {
        protocol.assertPacket(
            "1c00003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303",
            protocol.encode(MatchNewPacket(inboundMatchV535())),
        )
    }

    @Test
    fun `match join success includes scoring and team`() {
        protocol.assertPacket(
            "2500003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV535())),
        )
    }

    @Test
    fun `read match create includes scoring and team`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV535()),
            protocol.decode(
                hex(
                    "2000003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read match change settings includes scoring and team`() {
        assertEquals(
            MatchChangeSettingsPacket(inboundMatchV535()),
            protocol.decode(
                hex(
                    "2a00003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read older match without scoring info`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV504()),
            protocol.decode(
                hex(
                    "2000003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003"
                )
            ),
        )
    }

    @Test
    fun `match score update includes tag byte`() {
        protocol.assertPacket(
            "3100001c000000d204000002050001000000020001000000942600000c000300016407",
            protocol.encode(MatchScoreUpdatePacket(scoreV535())),
        )
    }

    @Test
    fun `read match score update includes tag byte`() {
        assertEquals(
            InboundMatchScoreUpdatePacket(scoreV535()),
            protocol.decode(
                hex("3000001c000000d204000002050001000000020001000000942600000c000300016407")
            ),
        )
    }

    @Test
    fun `spectate frames include tag byte`() {
        protocol.assertPacket(
            "1000003b00000002000200000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c000300016407",
            protocol.encode(SpectateFramesPacket(bundleV535())),
        )
    }

    @Test
    fun `read spectate frames include tag byte`() {
        assertEquals(
            InboundSpectateFramesPacket(bundleV535()),
            protocol.decode(
                hex(
                    "1300003b00000002000200000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c000300016407"
                )
            ),
        )
    }

    @Test
    fun `title update is empty`() {
        protocol.assertPacket("4d000000000000", protocol.encode(TitleUpdatePacket()))
    }

    @Test
    fun `b504 doesn't include match scoring`() {
        val older = Protocols.select(504)

        older.assertPacket(
            "2f00003800000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c08020202020202000000030000000200000003",
            older.encode(MatchStartPacket(inboundMatchV535())),
        )
    }

    @Test
    fun `b504 doesn't include score tag`() {
        val older = Protocols.select(504)

        older.assertPacket(
            "3100001b000000d204000002050001000000020001000000942600000c0003000164",
            older.encode(MatchScoreUpdatePacket(scoreV535())),
        )
    }

    @Test
    fun `b504 doesn't support title update`() {
        val older = Protocols.select(504)

        assertFailsWith<UnsupportedPacketException> { older.encode(TitleUpdatePacket()) }
    }
}

private fun inboundMatchV535(): Match =
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
            scoringType = ScoringType.ScoreV2,
            teamType = TeamType.TagTeamVs,
            freemod = false,
            seed = 0,
        )
        .withMinimumSlotCount(8)

private fun inboundMatchV504(): Match =
    inboundMatchV535()
        .copy(
            scoringType = ScoringType.Score,
            teamType = TeamType.HeadToHead,
        )

private fun scoreV535(): ScoreFrame =
    ScoreFrame(1234, 2, 5, 1, 0, 2, 1, 0, 9876, 12, 3, true, 100, tagByte = 7)

private fun bundleV535(): ReplayFrameBundle =
    ReplayFrameBundle(
        action = ReplayAction.Standard,
        frames =
            listOf(
                ReplayFrame(buttonState = ButtonState.Right1, mouseX = 0f, mouseY = 0f, time = 0),
                ReplayFrame(buttonState = ButtonState.Left1, mouseX = 0f, mouseY = 0f, time = 1),
            ),
        frame = scoreV535(),
    )
