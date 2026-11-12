package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
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

class B489Test {
    private val protocol = Protocols.select(489)
    private val info = testUserInfo(status = testStatus().copy(mode = Mode.OsuMania))

    @Test
    fun `user stats include mode`() {
        protocol.assertPacket(
            "0c00002b000000030000000100010b0449646c650000000300000000000000000000c8420100000000000000000000000100",
            protocol.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `user presence includes mode`() {
        protocol.assertPacket(
            "0c00005d000000030000000200010b0449646c650000000300000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `read user status includes mode`() {
        assertEquals(
            UserStatusPacket(
                UserStatus(
                    action = Status.Playing,
                    text = "idle",
                    mods = Mods.Hidden,
                    mode = Mode.OsuMania,
                    beatmapChecksum = "checksum",
                )
            ),
            protocol.decode(hex("0000001500000002010b0469646c650b08636865636b73756d080003")),
        )
    }

    @Test
    fun `protocol negotiation defaults to protocol version`() {
        protocol.assertPacket(
            "4c00000400000001000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `irc users skip mode encoding`() {
        val irc = testUserInfo(isIrc = true)

        protocol.assertPacket(
            "0b00000f0000000b0d746573745f757365726e616d65",
            protocol.encode(UserStatsPacket(irc)),
        )
        protocol.assertPacket(
            "0b00000f0000000b0d746573745f757365726e616d65",
            protocol.encode(UserPresencePacket(irc)),
        )
    }

    @Test
    fun `b487 user stats don't include mode`() {
        val older = Protocols.select(487)

        older.assertPacket(
            "0c00002a000000030000000100010b0449646c6500000000000000000000000000c8420100000000000000000000000100",
            older.encode(UserStatsPacket(info)),
        )
    }

    @Test
    fun `b487 user presence doesn't include mode`() {
        val older = Protocols.select(487)

        older.assertPacket(
            "0c00005c000000030000000200010b0449646c6500000000000000000000000000c84201000000000000000000000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(info)),
        )
    }
}
