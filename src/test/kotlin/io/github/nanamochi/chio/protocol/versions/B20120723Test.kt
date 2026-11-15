package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20120723Test {
    private val protocol = Protocols.select(20120723)

    @Test
    fun `user stats include performance points`() {
        val stats =
            testStats(rank = 100_000, accuracy = 99.5f, playcount = 42)
                .copy(
                    rankedScore = 1_000_000,
                    totalScore = 2_000_000,
                    performancePoints = 1234,
                )
        val status =
            testStatus(action = Status.Playing, text = "Playing")
                .copy(
                    beatmapChecksum = "abc",
                    beatmapId = 123,
                )

        protocol.assertPacket(
            "0b00003800000003000000020b07506c6179696e670b036162630000007b00000040420f00000000000000c7422a00000080841e0000000000a0860100d204",
            protocol.encode(UserStatsPacket(testUserInfo(status = status, stats = stats))),
        )
    }
}
