package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.SetIrcAwayMessagePacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B1600Test {
    private val protocol = Protocols.select(1600)

    @Test
    fun `read set irc away message`() {
        assertEquals(
            SetIrcAwayMessagePacket(Message(sender = "player", content = "away", target = "#osu")),
            protocol.decode(hex("530000140000000b06706c617965720b04617761790b04236f7375")),
        )
    }

    @Test
    fun `b1365 doesn't support set irc away message`() {
        val older = Protocols.select(1365)

        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("530000140000000b06706c617965720b04617761790b04236f7375"))
        }
    }
}
