package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B695Test {
    private val protocol = Protocols.select(695)
    private val info = testUserInfo(stats = testStats(rank = 100_000))

    @Test
    fun `user stats write rank as s32`() {
        protocol.assertPacket(
            "0c000031000000030000000100010b0449646c65000000000000000000000000000000000000c842010000000000000000000000a0860100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence writes rank as s32`() {
        protocol.assertPacket(
            "0c000063000000030000000200010b0449646c65000000000000000000000000000000000000c842010000000000000000000000a08601000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `b659 writes rank as u16`() {
        val older = Protocols.select(659)
        val olderInfo = testUserInfo(stats = testStats(rank = 1))

        older.assertPacket(
            "0c00002f000000030000000100010b0449646c65000000000000000000000000000000000000c8420100000000000000000000000100",
            older.encode(UserStatsPacket(olderInfo)),
        )
        older.assertPacket(
            "0c000061000000030000000200010b0449646c65000000000000000000000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(olderInfo)),
        )
    }
}
