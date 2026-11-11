package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B312Test {
    private val protocol = Protocols.select(312)

    @Test
    fun `match start ignores the match payload`() {
        protocol.assertPacket(
            "2e00140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(MatchStartPacket(Match())),
        )
    }

    @Test
    fun `match score update`() {
        protocol.assertPacket(
            "30004d0000001f8b08000000000002ffe356b04cb534334a344835b54cb134484c4ab6343749b334b34cb4303233314a4eb4e46200021e061606460666206662d820c5c0c00b6431b0020081888c613d000000",
            protocol.encode(
                MatchScoreUpdatePacket(ScoreFrame(10, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, 5))
            ),
        )
    }
}
