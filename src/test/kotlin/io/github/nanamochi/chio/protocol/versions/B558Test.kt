package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeTeamPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
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
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B558Test {
    private val protocol = Protocols.select(558)

    @Test
    fun `match start includes slot teams`() {
        protocol.assertPacket(
            "2f00004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchStartPacket(inboundMatchV558())),
        )
    }

    @Test
    fun `match update includes slot teams`() {
        protocol.assertPacket(
            "1b00004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchUpdatePacket(inboundMatchV558())),
        )
    }

    @Test
    fun `match new includes slot teams`() {
        protocol.assertPacket(
            "1c00004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchNewPacket(inboundMatchV558())),
        )
    }

    @Test
    fun `match join success includes slot teams`() {
        protocol.assertPacket(
            "2500004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV558())),
        )
    }

    @Test
    fun `read match create includes slot teams`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV558()),
            protocol.decode(
                hex(
                    "2000004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read match change settings includes slot teams`() {
        assertEquals(
            MatchChangeSettingsPacket(inboundMatchV558()),
            protocol.decode(
                hex(
                    "2a00004200000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `read match change team`() {
        assertEquals(
            MatchChangeTeamPacket,
            protocol.decode(hex("4e000000000000")),
        )
    }

    @Test
    fun `protocol negotiation defaults to version four`() {
        protocol.assertPacket(
            "4c00000400000004000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `match update and new skip ids above one byte`() {
        val match = inboundMatchV558().copy(id = 256)

        assertEquals(0, protocol.encode(MatchUpdatePacket(match)).size)
        assertEquals(0, protocol.encode(MatchNewPacket(match)).size)
    }

    @Test
    fun `b535 doesn't include slot teams`() {
        val older = Protocols.select(535)

        older.assertPacket(
            "2f00003a00000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000030303",
            older.encode(MatchStartPacket(inboundMatchV558())),
        )
    }

    @Test
    fun `b535 doesn't support match change team`() {
        val older = Protocols.select(535)

        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("4e000000000000"))
        }
    }
}

private fun inboundMatchV558(): Match =
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
