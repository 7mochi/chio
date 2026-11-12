package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.FriendsAddPacket
import io.github.nanamochi.chio.packet.inbound.FriendsRemovePacket
import io.github.nanamochi.chio.packet.model.UserInfo
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.FriendsListPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.types.Permissions
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B452Test {
    private val protocol = Protocols.select(452)

    @Test
    fun `user presence includes permissions`() {
        protocol.assertPacket(
            "0c00005c000000030000000200010b0449646c6500000000000000000000000000c842010000006469531f0400000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657325",
            protocol.encode(UserPresencePacket(testUserV452())),
        )
    }

    @Test
    fun `friends list`() {
        protocol.assertPacket(
            "490000100000000300000002000000030000002a000000",
            protocol.encode(FriendsListPacket(listOf(2, 3, 42))),
        )
    }

    @Test
    fun `read friends add`() {
        assertEquals(FriendsAddPacket(42), protocol.decode(hex("4a0000040000002a000000")))
    }

    @Test
    fun `read friends remove`() {
        assertEquals(FriendsRemovePacket(42), protocol.decode(hex("4b0000040000002a000000")))
    }

    @Test
    fun `b425 presence has no permissions`() {
        val older = Protocols.select(425)

        older.assertPacket(
            "0c00005b000000030000000200010b0449646c6500000000000000000000000000c842010000006469531f0400000001000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c6573",
            older.encode(UserPresencePacket(testUserV452())),
        )
    }

    @Test
    fun `b425 doesn't support friends`() {
        val older = Protocols.select(425)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(FriendsListPacket(listOf(2)))
        }
        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("4a0000040000002a000000"))
        }
        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("4b0000040000002a000000"))
        }
    }
}

private fun testUserV452(): UserInfo {
    val info = testUserInfo(stats = testStats().copy(totalScore = 20_000_000_000L))
    return info.copy(
        presence =
            info.presence.copy(
                permissions = Permissions.Regular + Permissions.Supporter + Permissions.Tournament
            )
    )
}
