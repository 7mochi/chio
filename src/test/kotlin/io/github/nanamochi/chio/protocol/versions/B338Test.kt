package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B338Test {
    private val protocol = Protocols.select(338)

    @Test
    fun `user stats normal user`() {
        protocol.assertPacket(
            "0c00002a000000030000000100010b0449646c6500000000000000000000000000c8420100000000000000000000000100",
            protocol.encode(UserStatsPacket(testUserInfo())),
        )
    }

    @Test
    fun `user presence normal user`() {
        protocol.assertPacket(
            "0c00005b000000030000000200010b0449646c6500000000000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c6573",
            protocol.encode(UserPresencePacket(testUserInfo())),
        )
    }

    @Test
    fun `user stats irc user`() {
        protocol.assertPacket(
            "0b00000f0000000b0d746573745f757365726e616d65",
            protocol.encode(UserStatsPacket(testUserInfo(isIrc = true))),
        )
    }

    @Test
    fun `read user status`() {
        assertEquals(
            UserStatusPacket(
                UserStatus(
                    action = Status.Playing,
                    text = "idle",
                    mods = Mods.Hidden,
                    beatmapChecksum = "checksum",
                )
            ),
            protocol.decode(hex("0000001400000000010b0469646c650b08636865636b73756d0800")),
        )
    }
}
