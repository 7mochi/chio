package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchCompletePacket
import io.github.nanamochi.chio.packet.inbound.MatchHasBeatmapPacket
import io.github.nanamochi.chio.packet.outbound.MatchAllPlayersLoadedPacket
import io.github.nanamochi.chio.packet.outbound.MatchTransferHostPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B340Test {
    private val protocol = Protocols.select(340)

    @Test
    fun `match transfer host uses b340 ids`() {
        protocol.assertPacket("33000000000000", protocol.encode(MatchTransferHostPacket))
    }

    @Test
    fun `match all players loaded uses b340 ids`() {
        protocol.assertPacket("36000000000000", protocol.encode(MatchAllPlayersLoadedPacket))
    }

    @Test
    fun `read match has beatmap`() {
        assertEquals(MatchHasBeatmapPacket, protocol.decode(hex("3c000000000000")))
    }

    @Test
    fun `raw 50 no longer maps to match change beatmap`() {
        assertEquals(MatchCompletePacket, protocol.decode(hex("32000000000000")))
    }
}
