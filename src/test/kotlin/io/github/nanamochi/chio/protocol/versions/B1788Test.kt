package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.ExitPacket
import io.github.nanamochi.chio.packet.inbound.UserStatsRequestPacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.FriendsListPacket
import io.github.nanamochi.chio.packet.outbound.IrcJoinPacket
import io.github.nanamochi.chio.packet.outbound.MatchPlayerSkippedPacket
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.packet.outbound.RestartPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B1788Test {
    private val protocol = Protocols.select(1788)
    private val status =
        UserStatus(
            action = Status.Playing,
            text = "playing",
            mods = Mods.HardRock + Mods.Hidden,
            mode = Mode.OsuMania,
            beatmapChecksum = "checksum",
            beatmapId = 1234567,
        )
    private val info =
        testUserInfo(
                status = status,
                stats =
                    testStats(rank = 100_000, accuracy = 98.5f, playcount = 42)
                        .copy(rankedScore = 987_654_321, totalScore = 30_000_000_000),
            )
            .let { it.copy(presence = it.presence.copy(longitude = 1.5, latitude = -2.5)) }
    private val ircInfo = info.copy(presence = info.presence.copy(isIrc = true))

    @Test
    fun `user stats use the b1788 layout`() {
        protocol.assertPacket(
            "0b00003b00000003000000020b07706c6179696e670b08636865636b73756d18000387d61200b168de3a000000000000c5422a00000000ac23fc06000000a0860100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence uses the b1788 layout`() {
        protocol.assertPacket(
            "53000034000000030000000b0d746573745f757365726e616d6501130b144e65746865726c616e647320416e74696c6c6573010000c03f000020c0",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `irc presence uses a negative id`() {
        protocol.assertPacket(
            "53000034000000fdffffff0b0d746573745f757365726e616d6501130b144e65746865726c616e647320416e74696c6c6573010000c03f000020c0",
            protocol.encode(UserPresencePacket(ircInfo)),
        )
    }

    @Test
    fun `irc stats keep the positive id`() {
        protocol.assertPacket(
            "0b00003b00000003000000020b07706c6179696e670b08636865636b73756d18000387d61200b168de3a000000000000c5422a00000000ac23fc06000000a0860100",
            protocol.encode(UserStatsPacket(ircInfo)),
        )
    }

    @Test
    fun `read user status without update flag`() {
        assertEquals(
            UserStatusPacket(status),
            protocol.decode(
                hex("0000001b000000020b07706c6179696e670b08636865636b73756d18000387d61200")
            ),
        )
    }

    @Test
    fun `friends list uses s16 count`() {
        protocol.assertPacket(
            "4800000a00000002000300000005000000",
            protocol.encode(FriendsListPacket(listOf(3, 5))),
        )
    }

    @Test
    fun `read user stats request`() {
        assertEquals(
            UserStatsRequestPacket(listOf(3, 5)),
            protocol.decode(hex("5500000a00000002000300000005000000")),
        )
    }

    @Test
    fun `restart sends retry delay`() {
        protocol.assertPacket(
            "56000004000000e8030000",
            protocol.encode(RestartPacket(1000)),
        )
    }

    @Test
    fun `read exit during update`() {
        assertEquals(
            ExitPacket(updating = true),
            protocol.decode(hex("0200000400000001000000")),
        )
    }

    @Test
    fun `read exit without update`() {
        assertEquals(
            ExitPacket(updating = false),
            protocol.decode(hex("0200000400000000000000")),
        )
    }

    @Test
    fun `protocol negotiation defaults to version six`() {
        protocol.assertPacket(
            "4b00000400000006000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `irc join is deprecated`() {
        protocol.assertPacket("", protocol.encode(IrcJoinPacket("player")))
    }

    @Test
    fun `match player skipped uses the direct packet id`() {
        protocol.assertPacket(
            "5100000400000003000000",
            protocol.encode(MatchPlayerSkippedPacket(slotId = 3)),
        )
    }
}
