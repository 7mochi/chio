package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.ChangeFriendOnlyDmsPacket
import io.github.nanamochi.chio.packet.outbound.UserDmsBlockedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20130131Test {
    private val protocol = Protocols.select(20130131)

    @Test
    fun `user dms blocked writes a username message`() {
        protocol.assertPacket(
            "6400001700000000000b0f746573745f757365726e616d655f32ffffffff",
            protocol.encode(UserDmsBlockedPacket("test_username_2")),
        )
    }

    @Test
    fun `read change friend only dms reads a boolean`() {
        assertEquals(
            ChangeFriendOnlyDmsPacket(true),
            protocol.decode(hex("6300000400000001000000")),
        )
    }
}
