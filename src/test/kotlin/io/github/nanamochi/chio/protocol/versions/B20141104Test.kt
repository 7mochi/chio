package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.MatchAbortPacket
import io.github.nanamochi.chio.packet.outbound.SwitchTournamentServerPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20141104Test {
    private val protocol = Protocols.select(20141104)

    @Test
    fun `switch tournament server writes the server`() {
        protocol.assertPacket(
            "6b0000150000000b13746f75726e65792e6578616d706c652e636f6d",
            protocol.encode(SwitchTournamentServerPacket("tourney.example.com")),
        )
    }

    @Test
    fun `match abort is empty`() {
        protocol.assertPacket("6a000000000000", protocol.encode(MatchAbortPacket))
    }
}
