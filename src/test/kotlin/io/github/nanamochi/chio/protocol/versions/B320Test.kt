package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.inbound.PrivateMessagePacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.outbound.MessagePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B320Test {
    private val protocol = Protocols.select(320)

    @Test
    fun `message to osu channel`() {
        protocol.assertPacket(
            "0700330000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d353b95994f38b4b011be2b5c023000000",
            protocol.encode(MessagePacket(Message("test_username", "test_message", "#osu", 0))),
        )
    }

    @Test
    fun `message with markdown link`() {
        protocol.assertPacket(
            "0700510000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5d6f248cdc9c957d070ad48cc2dc849d58cce28292928b6d2d74f8508e825e7e7c62a84e717e5a470b328e717970200f04843b441000000",
            protocol.encode(
                MessagePacket(
                    Message("test_username", "Hello [https://example.com Example] World", "#osu", 0)
                )
            ),
        )
    }

    @Test
    fun `read message`() {
        assertEquals(
            InboundMessagePacket(Message("test_username", "test_message", "#osu")),
            protocol.decode(
                hex(
                    "0100330000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d353b95994f38b4b011be2b5c023000000"
                )
            ),
        )
    }

    @Test
    fun `read private message`() {
        assertEquals(
            PrivateMessagePacket(Message("test_username", "private", "test_username_2")),
            protocol.decode(
                hex(
                    "1a00320000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5662f28ca2c4b2c49e5e647118f370200f18641d229000000"
                )
            ),
        )
    }
}
