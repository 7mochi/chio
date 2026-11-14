package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.InvitePacket as InboundInvitePacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.outbound.InvitePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B1817Test {
    private val protocol = Protocols.select(1817)
    private val message = Message("test_username", "test_message", "test_username_2", 3)

    @Test
    fun `invite is sent as a match invite`() {
        protocol.assertPacket(
            "5800002e0000000b0d746573745f757365726e616d650b0c746573745f6d6573736167650b0f746573745f757365726e616d655f32",
            protocol.encode(InvitePacket(message)),
        )
    }

    @Test
    fun `read invite reads a user id`() {
        assertEquals(
            InboundInvitePacket(100),
            protocol.decode(hex("5700000400000064000000")),
        )
    }

    @Test
    fun `b1800 sends invites as messages`() {
        Protocols.select(1800)
            .assertPacket(
                "0700002e0000000b0d746573745f757365726e616d650b0c746573745f6d6573736167650b0f746573745f757365726e616d655f32",
                Protocols.select(1800).encode(InvitePacket(message)),
            )
    }
}
