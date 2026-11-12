package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Rank
import io.github.nanamochi.chio.packet.types.RankedStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B504Test {
    private val protocol = Protocols.select(504)
    private val info =
        testUserInfo(
            status = testStatus().copy(mode = Mode.OsuMania, beatmapId = 1234567),
            stats = testStats().copy(totalScore = 30_000_000_000L),
        )
    private val reply =
        BeatmapInfoReplyPacket(
            listOf(
                BeatmapInfo(
                    index = 0,
                    beatmapId = 1234567,
                    beatmapSetId = 7654321,
                    threadId = 42,
                    rankedStatus = RankedStatus.Ranked,
                    osuRank = Rank.A,
                    checksum = "checksum",
                    fruitsRank = Rank.XH,
                    taikoRank = Rank.S,
                ),
                BeatmapInfo(
                    index = 1,
                    beatmapId = -1,
                    beatmapSetId = 0,
                    threadId = -1,
                    rankedStatus = RankedStatus.Pending,
                    osuRank = Rank.N,
                    checksum = "",
                    fruitsRank = Rank.N,
                    taikoRank = Rank.N,
                ),
            )
        )

    @Test
    fun `beatmap info reply includes extra ranks`() {
        protocol.assertPacket(
            "4600003300000002000000000087d61200b1cb74002a000000010400030b08636865636b73756d0100ffffffff00000000ffffffff0009090900",
            protocol.encode(reply),
        )
    }

    @Test
    fun `user stats use the b504 total score cap`() {
        protocol.assertPacket(
            "0c00002f000000030000000100010b0449646c650000000387d6120000000000000000000000c842010000002b5c3945060000000100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence uses the b504 total score cap`() {
        protocol.assertPacket(
            "0c000061000000030000000200010b0449646c650000000387d6120000000000000000000000c842010000002b5c39450600000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `protocol negotiation defaults to version two`() {
        protocol.assertPacket(
            "4c00000400000002000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `b490 reply omits extra ranks`() {
        val older = Protocols.select(490)

        older.assertPacket(
            "4600002f00000002000000000087d61200b1cb74002a00000001040b08636865636b73756d0100ffffffff00000000ffffffff000900",
            older.encode(reply),
        )
    }

    @Test
    fun `b490 stats use the older total score cap`() {
        val older = Protocols.select(490)

        older.assertPacket(
            "0c00002f000000030000000100010b0449646c650000000387d6120000000000000000000000c842010000006469531f040000000100",
            older.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `b490 presence uses the older total score cap`() {
        val older = Protocols.select(490)

        older.assertPacket(
            "0c000061000000030000000200010b0449646c650000000387d6120000000000000000000000c842010000006469531f0400000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(info)),
        )
    }
}
