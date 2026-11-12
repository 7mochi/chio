package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B365Test {
    private val protocol = Protocols.select(365)
    private val info = testUserInfo(stats = testStats().copy(totalScore = 20_000_000_000L))

    @Test
    fun `user stats caps total score`() {
        protocol.assertPacket(
            "0c00002a000000030000000100010b0449646c6500000000000000000000000000c842010000006469531f040000000100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence caps total score`() {
        protocol.assertPacket(
            "0c00005b000000030000000200010b0449646c6500000000000000000000000000c842010000006469531f0400000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c6573",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `b354 doesn't cap total score`() {
        val older = Protocols.select(354)

        older.assertPacket(
            "0c00002a000000030000000100010b0449646c6500000000000000000000000000c8420100000000c817a8040000000100",
            older.encode(UserStatsPacket(info)),
        )
    }
}
