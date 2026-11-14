package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.ChannelLeavePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B613Test {
    private val protocol = Protocols.select(613)

    @Test
    fun `read channel leave`() {
        assertEquals(
            ChannelLeavePacket("#osu"),
            protocol.decode(hex("4f0000060000000b04236f7375")),
        )
    }

    @Test
    fun `read empty channel leave`() {
        assertEquals(
            ChannelLeavePacket(""),
            protocol.decode(hex("4f00000100000000")),
        )
    }

    @Test
    fun `b591 doesn't support channel leave`() {
        val older = Protocols.select(591)

        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("4f0000060000000b04236f7375"))
        }
    }
}
