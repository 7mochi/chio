package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B659Test {
    private val protocol = Protocols.select(659)
    private val info =
        testUserInfo(
            status = testStatus(),
            stats = testStats().copy(totalScore = 30_000_000_000L),
        )

    @Test
    fun `user stats don't cap total score`() {
        protocol.assertPacket(
            "0c00002f000000030000000100010b0449646c65000000000000000000000000000000000000c8420100000000ac23fc060000000100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence doesn't cap total score`() {
        protocol.assertPacket(
            "0c000061000000030000000200010b0449646c65000000000000000000000000000000000000c8420100000000ac23fc0600000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `b634 stats cap total score`() {
        val older = Protocols.select(634)

        older.assertPacket(
            "0c00002f000000030000000100010b0449646c65000000000000000000000000000000000000c842010000002b5c3945060000000100",
            older.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `b634 presence cap total score`() {
        val older = Protocols.select(634)

        older.assertPacket(
            "0c000061000000030000000200010b0449646c65000000000000000000000000000000000000c842010000002b5c39450600000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(info)),
        )
    }
}
