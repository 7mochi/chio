package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.TournamentMatchInfoPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20121207Test {
    private val protocol = Protocols.select(20121207)

    @Test
    fun `read tournament match info reads a match id`() {
        assertEquals(
            TournamentMatchInfoPacket(123),
            protocol.decode(hex("5d0000040000007b000000")),
        )
    }
}
