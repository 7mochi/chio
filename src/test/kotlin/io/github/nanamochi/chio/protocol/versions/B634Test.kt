package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.ReceiveUpdatesPacket
import io.github.nanamochi.chio.packet.outbound.MonitorPacket
import io.github.nanamochi.chio.packet.types.PresenceFilter
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B634Test {
    private val protocol = Protocols.select(634)

    @Test
    fun `monitor is empty`() {
        protocol.assertPacket("51000000000000", protocol.encode(MonitorPacket))
    }

    @Test
    fun `read receive updates`() {
        assertEquals(
            ReceiveUpdatesPacket(PresenceFilter.Friends),
            protocol.decode(hex("5000000400000002000000")),
        )
    }

    @Test
    fun `b613 doesn't support monitor`() {
        val older = Protocols.select(613)

        assertFailsWith<UnsupportedPacketException> { older.encode(MonitorPacket) }
    }

    @Test
    fun `b613 doesn't support receive updates`() {
        val older = Protocols.select(613)

        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("5000000400000002000000"))
        }
    }
}
