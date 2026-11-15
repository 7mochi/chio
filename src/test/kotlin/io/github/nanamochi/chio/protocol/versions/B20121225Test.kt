package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.outbound.MessagePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20121225Test {
    private val protocol = Protocols.select(20121225)
    private val message = Message("test_username", "test_message", "#osu", 3)

    @Test
    fun `message writes the sender id`() {
        protocol.assertPacket(
            "070000270000000b0d746573745f757365726e616d650b0c746573745f6d6573736167650b04236f737503000000",
            protocol.encode(MessagePacket(message)),
        )
    }

    @Test
    fun `read message reads the sender id`() {
        assertEquals(
            InboundMessagePacket(message),
            protocol.decode(
                hex(
                    "010000270000000b0d746573745f757365726e616d650b0c746573745f6d6573736167650b04236f737503000000"
                )
            ),
        )
    }
}
