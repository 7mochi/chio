package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.outbound.LoginPermissionsPacket
import io.github.nanamochi.chio.packet.types.Permissions
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B425Test {
    private val protocol = Protocols.select(425)

    @Test
    fun `login permissions`() {
        protocol.assertPacket(
            "4800000400000025000000",
            protocol.encode(
                LoginPermissionsPacket(
                    Permissions.Regular + Permissions.Supporter + Permissions.Tournament
                )
            ),
        )
    }

    @Test
    fun `b402 doesn't support login permissions`() {
        val older = Protocols.select(402)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(LoginPermissionsPacket(Permissions.Regular))
        }
    }
}
