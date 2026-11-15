package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.PresenceRequestAllPacket
import io.github.nanamochi.chio.packet.inbound.PresenceRequestPacket
import io.github.nanamochi.chio.packet.model.UserQuit
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresenceBundlePacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceSinglePacket
import io.github.nanamochi.chio.packet.outbound.UserQuitPacket
import io.github.nanamochi.chio.packet.types.QuitState
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20121224Test {
    private val protocol = Protocols.select(20121224)
    private val info = testUserInfo()

    @Test
    fun `user quit writes the user id and state`() {
        protocol.assertPacket(
            "0c0000050000000300000000",
            protocol.encode(UserQuitPacket(UserQuit(info, QuitState.Gone))),
        )
    }

    @Test
    fun `presence bundle writes user ids`() {
        protocol.assertPacket(
            "6000000a00000002000300000003000000",
            protocol.encode(UserPresenceBundlePacket(listOf(info, info))),
        )
    }

    @Test
    fun `presence single writes a user id`() {
        protocol.assertPacket(
            "5f00000400000003000000",
            protocol.encode(UserPresenceSinglePacket(info)),
        )
    }

    @Test
    fun `read presence request reads user ids`() {
        assertEquals(
            PresenceRequestPacket(listOf(3, 4)),
            protocol.decode(hex("6100000a00000002000300000004000000")),
        )
    }

    @Test
    fun `read presence request all is empty`() {
        assertEquals(
            PresenceRequestAllPacket,
            protocol.decode(hex("62000000000000")),
        )
    }
}
