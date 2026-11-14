package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.outbound.ChannelInfoCompletePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B20120518Test {
    private val protocol = Protocols.select(20120518)

    @Test
    fun `channel info complete is empty`() {
        protocol.assertPacket("59000000000000", protocol.encode(ChannelInfoCompletePacket))
    }

    @Test
    fun `b1820 doesn't support channel info complete`() {
        val older = Protocols.select(1820)

        assertFailsWith<UnsupportedPacketException> { older.encode(ChannelInfoCompletePacket) }
    }
}
