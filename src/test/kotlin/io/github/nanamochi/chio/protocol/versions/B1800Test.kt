package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B1800Test {
    private val protocol = Protocols.select(1800)
    private val match =
        Match(id = 256, inProgress = true, name = "x".repeat(200), beatmapId = 0, hostId = 2)
            .withMinimumSlotCount(8)

    @Test
    fun `large packets stay uncompressed`() {
        protocol.assertPacket(
            "1a0000ef0000000001010000000bc8017878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878000000000000000202020202020202000000000000000002000000000000",
            protocol.encode(MatchUpdatePacket(match)),
        )
    }

    @Test
    fun `b1797 still compresses large packets`() {
        val encoded = Protocols.select(1797).encode(MatchUpdatePacket(match))

        assertEquals(1, encoded[2].toInt())
    }
}
