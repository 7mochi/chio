package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20250306Test {
    private val protocol = Protocols.select(20250306)

    @Test
    fun `user stats writes performance points as u16`() {
        val stats =
            testStats(rank = 100_000, accuracy = 99.5f, playcount = 42)
                .copy(
                    rankedScore = 1_000_000,
                    totalScore = 2_000_000,
                    performancePoints = 70_000,
                )
        val status =
            testStatus(action = Status.Playing, text = "Playing")
                .copy(
                    beatmapChecksum = "abc",
                    beatmapId = 123,
                )

        protocol.assertPacket(
            "0b00003a00000003000000020b07506c6179696e670b0361626300000000007b00000040420f00000000000000c7422a00000080841e0000000000a0860100ffff",
            protocol.encode(UserStatsPacket(testUserInfo(status = status, stats = stats))),
        )
    }
}
