package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchChangeBeatmapPacket as InboundMatchChangeBeatmapPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeModsPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket as InboundMatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket as InboundMatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchFailedPacket
import io.github.nanamochi.chio.packet.inbound.MatchLoadCompletePacket
import io.github.nanamochi.chio.packet.inbound.MatchNoBeatmapPacket
import io.github.nanamochi.chio.packet.inbound.MatchNotReadyPacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.AnnouncePacket
import io.github.nanamochi.chio.packet.outbound.MatchAllPlayersLoadedPacket
import io.github.nanamochi.chio.packet.outbound.MatchCompletePacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchPlayerFailedPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchTransferHostPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
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
            "2f00002500000001000000000b04546573740b03313233000000000001040802020202020200000003000000",
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

    @Test
    fun `read match change mods`() {
        assertEquals(
            MatchChangeModsPacket(Mods.HardRock + Mods.Hidden),
            protocol.decode(hex("3500000400000018000000")),
        )
    }

    @Test
    fun `read match load complete`() {
        assertEquals(
            MatchLoadCompletePacket,
            protocol.decode(hex("36000000000000")),
        )
    }

    @Test
    fun `read match no beatmap`() {
        assertEquals(
            MatchNoBeatmapPacket,
            protocol.decode(hex("38000000000000")),
        )
    }

    @Test
    fun `read match not ready`() {
        assertEquals(
            MatchNotReadyPacket,
            protocol.decode(hex("39000000000000")),
        )
    }

    @Test
    fun `read match failed`() {
        assertEquals(
            MatchFailedPacket,
            protocol.decode(hex("3a000000000000")),
        )
    }

    @Test
    fun `read match create`() {
        assertEquals(
            InboundMatchCreatePacket(inboundMatchV334()),
            protocol.decode(
                hex(
                    "2000003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000"
                )
            ),
        )
    }

    @Test
    fun `read match change settings`() {
        assertEquals(
            InboundMatchChangeSettingsPacket(inboundMatchV334()),
            protocol.decode(
                hex(
                    "2a00003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000"
                )
            ),
        )
    }

    @Test
    fun `read match change beatmap`() {
        assertEquals(
            InboundMatchChangeBeatmapPacket(inboundMatchV334()),
            protocol.decode(
                hex(
                    "3300003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000"
                )
            ),
        )
    }

    @Test
    fun `read match score update`() {
        assertEquals(
            InboundMatchScoreUpdatePacket(scoreV334()),
            protocol.decode(
                hex("3000001b000000d204000002050001000000020001000000942600000c0003000164")
            ),
        )
    }

    @Test
    fun `read spectate frames`() {
        assertEquals(
            InboundSpectateFramesPacket(bundleV334()),
            protocol.decode(
                hex(
                    "1300003a00000002000001000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c0003000164"
                )
            ),
        )
    }

    @Test
    fun `spectate frames`() {
        protocol.assertPacket(
            "1000003a00000002000001000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c0003000164",
            protocol.encode(SpectateFramesPacket(bundleV334())),
        )
    }

    @Test
    fun `match update`() {
        protocol.assertPacket(
            "1b00003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000",
            protocol.encode(MatchUpdatePacket(inboundMatchV334())),
        )
    }

    @Test
    fun `match new`() {
        protocol.assertPacket(
            "1c00003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000",
            protocol.encode(MatchNewPacket(inboundMatchV334())),
        )
    }

    @Test
    fun `match join success`() {
        protocol.assertPacket(
            "2500003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV334())),
        )
    }

    @Test
    fun `match update with id over 255 is dropped`() {
        assertEquals(
            0,
            protocol.encode(MatchUpdatePacket(inboundMatchV334().copy(id = 256))).size,
        )
    }

    @Test
    fun `match player failed outside the slot range is dropped`() {
        assertEquals(0, protocol.encode(MatchPlayerFailedPacket(8)).size)
    }

    @Test
    fun `score update outside the slot range is dropped`() {
        val frame = ScoreFrame(0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0)
        assertEquals(0, protocol.encode(MatchScoreUpdatePacket(frame)).size)
    }

    @Test
    fun `announce over the compression threshold is compressed`() {
        protocol.assertPacket(
            "1900011a0000001f8b08000000000002ffe33ec198384c00008aa6c323cb000000",
            protocol.encode(AnnouncePacket("a".repeat(200))),
        )
    }

    @Test
    fun `compression threshold is measured on the payload`() {
        assertEquals(0, protocol.encode(AnnouncePacket("a".repeat(147)))[2].toInt())
        assertEquals(1, protocol.encode(AnnouncePacket("a".repeat(148)))[2].toInt())
    }

    @Test
    fun `match with extra slots is truncated to the protocol slot size`() {
        protocol.assertPacket(
            "1b00002b00000003010018000b0a54657374206d617463680b0b54657374202d205465737400000000000101010101010101",
            protocol.encode(MatchUpdatePacket(matchWithExtraSlotsV334())),
        )
    }
}

private fun inboundMatchV334(): Match =
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
            hostId = -1,
            mode = Mode.Osu,
            scoringType = ScoringType.Score,
            teamType = TeamType.HeadToHead,
            freemod = false,
            seed = 0,
        )
        .withMinimumSlotCount(8)

private fun scoreV334(): ScoreFrame = ScoreFrame(1234, 2, 5, 1, 0, 2, 1, 0, 9876, 12, 3, true, 100)

private fun bundleV334(): ReplayFrameBundle =
    ReplayFrameBundle(
        action = ReplayAction.Standard,
        frames =
            listOf(
                ReplayFrame(buttonState = ButtonState.Right1, mouseX = 0f, mouseY = 0f, time = 0),
                ReplayFrame(buttonState = ButtonState.Left1, mouseX = 0f, mouseY = 0f, time = 1),
            ),
        frame = scoreV334(),
    )

private fun matchWithExtraSlotsV334(): Match =
    inboundMatchV334()
        .copy(
            slots =
                List(8) { MatchSlot(it + 1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod) } +
                    MatchSlot(99, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod)
        )
