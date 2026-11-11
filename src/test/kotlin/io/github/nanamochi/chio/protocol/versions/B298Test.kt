package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.outbound.FellowSpectatorJoinedPacket
import io.github.nanamochi.chio.packet.outbound.FellowSpectatorLeftPacket
import io.github.nanamochi.chio.packet.outbound.LobbyJoinPacket
import io.github.nanamochi.chio.packet.outbound.LobbyPartPacket
import io.github.nanamochi.chio.packet.outbound.MatchDisbandPacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinFailPacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
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
import org.junit.jupiter.api.Test

class B298Test {
    private val protocol = Protocols.select(298)

    @Test
    fun `match update`() {
        protocol.assertPacket(
            "1b003b0000001f8b08000000000002ff6364e01608492d2e51c82dcd29c92cc849ac4c2de2e6068be82a80280610606463600252cc0c0c0c003fe44b1b31000000",
            protocol.encode(MatchUpdatePacket(matchOne())),
        )
    }

    @Test
    fun `match new`() {
        protocol.assertPacket(
            "1c003b0000001f8b08000000000002ff6362e01608492d2e51c82dcd29c92cc849ac4c2de2e6068be82a80280610606463600252cc0c0c0c004b93d25131000000",
            protocol.encode(MatchNewPacket(matchTwo())),
        )
    }

    @Test
    fun `match disband`() {
        protocol.assertPacket(
            "1d00180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(MatchDisbandPacket(3)),
        )
    }

    @Test
    fun `lobby join`() {
        protocol.assertPacket(
            "2300180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(LobbyJoinPacket(3)),
        )
    }

    @Test
    fun `lobby part`() {
        protocol.assertPacket(
            "2400180000001f8b08000000000002ff6361606000004b4826ae04000000",
            protocol.encode(LobbyPartPacket(4)),
        )
    }

    @Test
    fun `match join success`() {
        protocol.assertPacket(
            "25003e0000001f8b08000000000002ff6366e01608492d2e51c82dcd29c92cc849ac4c2de2e6068be82a8028061060e463600252cc40ccc2c0c0000056f985ff35000000",
            protocol.encode(MatchJoinSuccessPacket(matchThree())),
        )
    }

    @Test
    fun `match join fail`() {
        protocol.assertPacket(
            "2600140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(MatchJoinFailPacket),
        )
    }

    @Test
    fun `fellow spectator joined`() {
        protocol.assertPacket(
            "2b00180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(FellowSpectatorJoinedPacket(3)),
        )
    }

    @Test
    fun `fellow spectator left`() {
        protocol.assertPacket(
            "2c00180000001f8b08000000000002ff6365606000002e2f9a1604000000",
            protocol.encode(FellowSpectatorLeftPacket(5)),
        )
    }
}

internal fun matchOne(): Match =
    Match(
        id = 1,
        inProgress = false,
        type = MatchType.Standard,
        mods = Mods.NoMod,
        name = "Test multiplayer",
        password = "123",
        beatmapText = "Test - Test",
        beatmapId = 0,
        beatmapChecksum = "",
        slots =
            listOf(
                MatchSlot(1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                MatchSlot(2, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.HardRock),
                MatchSlot(3, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.Hidden + Mods.HardRock),
            ),
        hostId = 2,
        mode = Mode.Osu,
        scoringType = ScoringType.Score,
        teamType = TeamType.HeadToHead,
        freemod = false,
        seed = 0,
    )

private fun matchTwo(): Match =
    matchOne()
        .copy(
            id = 2,
            inProgress = true,
            mods = Mods(522171579),
            slots =
                listOf(
                    MatchSlot(1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(
                        2,
                        SlotStatus.HasPlayer,
                        SlotTeam.Neutral,
                        Mods.Hidden + Mods.HardRock,
                    ),
                    MatchSlot(3, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.Flashlight),
                ),
            hostId = 3,
            freemod = true,
        )

private fun matchThree(): Match =
    matchOne()
        .copy(
            id = 3,
            slots =
                listOf(
                    MatchSlot(1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(2, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(3, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod),
                    MatchSlot(4, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod),
                ),
            hostId = 3,
        )
