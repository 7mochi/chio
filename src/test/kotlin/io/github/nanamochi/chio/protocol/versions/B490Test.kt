package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.BeatmapInfoRequestPacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
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

class B490Test {
    private val protocol = Protocols.select(490)
    private val info =
        testUserInfo(status = testStatus().copy(mode = Mode.OsuMania, beatmapId = 1234567))

    @Test
    fun `user stats include beatmap id`() {
        protocol.assertPacket(
            "0c00002f000000030000000100010b0449646c650000000387d6120000000000000000000000c8420100000000000000000000000100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence includes beatmap id`() {
        protocol.assertPacket(
            "0c000061000000030000000200010b0449646c650000000387d6120000000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `read user status includes beatmap id`() {
        assertEquals(
            UserStatusPacket(
                UserStatus(
                    action = Status.Playing,
                    text = "idle",
                    mods = Mods.Hidden,
                    mode = Mode.OsuMania,
                    beatmapChecksum = "checksum",
                    beatmapId = 1234567,
                )
            ),
            protocol.decode(
                hex("0000001900000002010b0469646c650b08636865636b73756d08000387d61200")
            ),
        )
    }

    @Test
    fun `read beatmap info request includes ids`() {
        assertEquals(
            BeatmapInfoRequestPacket(listOf("a.osu", "b.osu"), listOf(2, 3, 42)),
            protocol.decode(
                hex(
                    "45000022000000020000000b05612e6f73750b05622e6f73750300000002000000030000002a000000"
                )
            ),
        )
    }

    @Test
    fun `b489 user stats don't include beatmap id`() {
        val older = Protocols.select(489)

        older.assertPacket(
            "0c00002b000000030000000100010b0449646c650000000300000000000000000000c8420100000000000000000000000100",
            older.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `b489 user presence doesn't include beatmap id`() {
        val older = Protocols.select(489)

        older.assertPacket(
            "0c00005d000000030000000200010b0449646c650000000300000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `b489 ignores beatmap set ids`() {
        val older = Protocols.select(489)

        assertEquals(
            BeatmapInfoRequestPacket(listOf("a.osu", "b.osu")),
            older.decode(
                hex(
                    "45000022000000020000000b05612e6f73750b05622e6f73750300000002000000030000002a000000"
                )
            ),
        )
    }
}
