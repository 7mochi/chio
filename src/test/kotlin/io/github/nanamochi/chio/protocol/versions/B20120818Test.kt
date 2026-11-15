package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20120818Test {
    private val protocol = Protocols.select(20120818)
    private val mods = Mods.HardRock + Mods.Hidden
    private val status =
        testStatus(action = Status.Playing, text = "Playing")
            .copy(
                beatmapChecksum = "abc",
                beatmapId = 123,
                mods = mods,
            )
    private val info =
        testUserInfo(
                status = status,
                stats =
                    testStats(rank = 100_000, accuracy = 99.5f, playcount = 42)
                        .copy(
                            rankedScore = 1_000_000,
                            totalScore = 2_000_000,
                            performancePoints = 1234,
                        ),
            )
            .let {
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

    @Test
    fun `user stats write mods as u32`() {
        protocol.assertPacket(
            "0b00003a00000003000000020b07506c6179696e670b0361626318000000007b00000040420f00000000000000c7422a00000080841e0000000000a0860100d204",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `read user status reads mods as u32`() {
        assertEquals(
            UserStatusPacket(status),
            protocol.decode(hex("00000018000000020b07506c6179696e670b0361626318000000007b000000")),
        )
    }

    @Test
    fun `match writes mods as u32`() {
        protocol.assertPacket(
            "2e00004500000000010100180000000b0a54657374206d617463680b067365637265740b0b54657374202d205465737400000000000202020202020202000000000000000002000000000000",
            protocol.encode(MatchStartPacket(match())),
        )
    }

    @Test
    fun `read match reads mods as u32`() {
        assertEquals(
            MatchCreatePacket(match()),
            protocol.decode(
                hex(
                    "1f00004500000000010100180000000b0a54657374206d617463680b067365637265740b0b54657374202d205465737400000000000202020202020202000000000000000002000000000000"
                )
            ),
        )
    }
}

private fun match(): Match =
    Match(
            id = 256,
            inProgress = true,
            mods = Mods.HardRock + Mods.Hidden,
            name = "Test match",
            password = "secret",
            beatmapText = "Test - Test",
            beatmapId = 0,
            beatmapChecksum = "",
            hostId = 2,
        )
        .withMinimumSlotCount(8)
