package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.LobbyJoinPacket
import io.github.nanamochi.chio.packet.outbound.LobbyPartPacket
import io.github.nanamochi.chio.protocol.Protocols
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20121211Test {
    private val protocol = Protocols.select(20121211)

    @Test
    fun `lobby join is dropped`() {
        assertEquals(0, protocol.encode(LobbyJoinPacket(5)).size)
    }

    @Test
    fun `lobby part is dropped`() {
        assertEquals(0, protocol.encode(LobbyPartPacket(5)).size)
    }
}
