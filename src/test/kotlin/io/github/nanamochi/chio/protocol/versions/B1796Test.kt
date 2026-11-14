package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
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

class B1796Test {
    private val protocol = Protocols.select(1796)

    @Test
    fun `match start writes a u16 id`() {
        protocol.assertPacket(
            "2e00004b0000000001010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchStartPacket(inboundMatchV1796())),
        )
    }

    @Test
    fun `match update writes a u16 id`() {
        protocol.assertPacket(
            "1a00004b0000000001010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchUpdatePacket(inboundMatchV1796())),
        )
    }

    @Test
    fun `match new writes a u16 id`() {
        protocol.assertPacket(
            "1b00004b0000000001010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchNewPacket(inboundMatchV1796())),
        )
    }

    @Test
    fun `match join success writes a u16 id`() {
        protocol.assertPacket(
            "2400004b0000000001010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303",
            protocol.encode(MatchJoinSuccessPacket(inboundMatchV1796())),
        )
    }

    @Test
    fun `read match create reads a u16 id`() {
        assertEquals(
            MatchCreatePacket(inboundMatchV1796()),
            protocol.decode(
                hex(
                    "1f00004b0000000001010018000b0a54657374206d617463680b067365637265740b0b54657374202d20546573740000000000017c0802020202020001020000000000020000000300000002000000030303"
                )
            ),
        )
    }

    @Test
    fun `match update and new skip ids above u16`() {
        val match = inboundMatchV1796().copy(id = 0x10000)

        assertEquals(0, protocol.encode(MatchUpdatePacket(match)).size)
        assertEquals(0, protocol.encode(MatchNewPacket(match)).size)
    }

    @Test
    fun `b1788 skips u16 match ids`() {
        val older = Protocols.select(1788)

        assertEquals(0, older.encode(MatchUpdatePacket(inboundMatchV1796())).size)
        assertEquals(0, older.encode(MatchNewPacket(inboundMatchV1796())).size)
    }

    @Test
    fun `user presence sends country index and city`() {
        val info =
            testUserInfo(stats = testStats()).let {
                it.copy(
                    presence =
                        it.presence.copy(
                            countryIndex = 10,
                            city = "Test City",
                            longitude = 1.5,
                            latitude = -2.5,
                        )
                )
            }

        protocol.assertPacket(
            "5300002a000000030000000b0d746573745f757365726e616d6501130a0b09546573742043697479010000c03f000020c0",
            protocol.encode(UserPresencePacket(info)),
        )
    }
}

private fun inboundMatchV1796(): Match =
    Match(
            id = 256,
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
