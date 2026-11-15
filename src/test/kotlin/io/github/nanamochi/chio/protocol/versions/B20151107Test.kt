package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.TournamentJoinMatchChannelPacket
import io.github.nanamochi.chio.packet.inbound.TournamentLeaveMatchChannelPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20151107Test {
    private val protocol = Protocols.select(20151107)

    @Test
    fun `read tournament join match channel reads a match id`() {
        assertEquals(
            TournamentJoinMatchChannelPacket(123),
            protocol.decode(hex("6c0000040000007b000000")),
        )
    }

    @Test
    fun `read tournament leave match channel reads a match id`() {
        assertEquals(
            TournamentLeaveMatchChannelPacket(123),
            protocol.decode(hex("6d0000040000007b000000")),
        )
    }
}
