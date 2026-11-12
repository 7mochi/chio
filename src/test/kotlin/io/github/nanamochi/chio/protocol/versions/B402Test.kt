package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchTransferHostRequestPacket
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
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B402Test {
    private val protocol = Protocols.select(402)

    @Test
    fun `match start uses host id`() {
        protocol.assertPacket(
            "2f00003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000",
            protocol.encode(MatchStartPacket(inboundMatchV402())),
        )
    }

    @Test
    fun `match update uses host id`() {
        protocol.assertPacket(
            "1b00003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000",
            protocol.encode(MatchUpdatePacket(inboundMatchV402())),
        )
    }

    @Test
    fun `match new uses host id`() {
        protocol.assertPacket(
            "1c00003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000",
            protocol.encode(MatchNewPacket(inboundMatchV402())),
        )
    }

    @Test
    fun `match join success uses host id`() {
        protocol.assertPacket(
            "2500003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV402())),
        )
    }

    @Test
    fun `read match create`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV402()),
            protocol.decode(
                hex(
                    "2000003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000"
                )
            ),
        )
    }

    @Test
    fun `read match change settings`() {
        assertEquals(
            MatchChangeSettingsPacket(inboundMatchV402()),
            protocol.decode(
                hex(
                    "2a00003700000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c080202020202020000000300000002000000"
                )
            ),
        )
    }

    @Test
    fun `read match transfer host`() {
        assertEquals(
            MatchTransferHostRequestPacket(2),
            protocol.decode(hex("4700000400000002000000")),
        )
    }

    @Test
    fun `match update and new skip ids above one byte`() {
        val match = inboundMatchV402().copy(id = 256)

        assertEquals(0, protocol.encode(MatchUpdatePacket(match)).size)
        assertEquals(0, protocol.encode(MatchNewPacket(match)).size)
    }

    @Test
    fun `b388 doesn't append host id`() {
        val older = Protocols.select(388)

        older.assertPacket(
            "1c00003300000003010018000b0a54657374206d617463680b0b54657374202d20546573740000000000017c0802020202020200000003000000",
            older.encode(MatchNewPacket(inboundMatchV402())),
        )
    }

    @Test
    fun `b388 doesn't support match transfer host`() {
        val older = Protocols.select(388)

        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("4700000400000002000000"))
        }
    }
}

private fun inboundMatchV402(): Match =
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
            mode = Mode.Osu,
            scoringType = ScoringType.Score,
            teamType = TeamType.HeadToHead,
            freemod = false,
            seed = 0,
        )
        .withMinimumSlotCount(8)
