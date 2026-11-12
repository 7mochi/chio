package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.ChannelJoinPacket
import io.github.nanamochi.chio.packet.model.Channel
import io.github.nanamochi.chio.packet.outbound.ChannelAvailableAutojoinPacket
import io.github.nanamochi.chio.packet.outbound.ChannelAvailablePacket
import io.github.nanamochi.chio.packet.outbound.ChannelJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.ChannelRevokedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B349Test {
    private val protocol = Protocols.select(349)

    @Test
    fun `channel join success`() {
        protocol.assertPacket(
            "410000060000000b04236f7375",
            protocol.encode(ChannelJoinSuccessPacket("#osu")),
        )
    }

    @Test
    fun `channel available`() {
        protocol.assertPacket(
            "420000060000000b04236f7375",
            protocol.encode(ChannelAvailablePacket(Channel("#osu"))),
        )
    }

    @Test
    fun `channel revoked`() {
        protocol.assertPacket(
            "430000060000000b04236f7375",
            protocol.encode(ChannelRevokedPacket("#osu")),
        )
    }

    @Test
    fun `channel available autojoin`() {
        protocol.assertPacket(
            "440000060000000b04236f7375",
            protocol.encode(ChannelAvailableAutojoinPacket(Channel("#osu"))),
        )
    }

    @Test
    fun `read channel join`() {
        assertEquals(ChannelJoinPacket("#osu"), protocol.decode(hex("400000060000000b04236f7375")))
    }

    @Test
    fun `b342 doesn't support channel join`() {
        val older = Protocols.select(342)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(ChannelJoinSuccessPacket("#osu"))
        }
        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("400000060000000b04236f7375"))
        }
    }
}
