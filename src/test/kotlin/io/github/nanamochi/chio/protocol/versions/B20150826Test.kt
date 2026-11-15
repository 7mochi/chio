package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.types.Rank
import io.github.nanamochi.chio.packet.types.RankedStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20150826Test {
    private val protocol = Protocols.select(20150826)

    @Test
    fun `qualified beatmaps are sent as qualified`() {
        val beatmap =
            BeatmapInfo(
                index = 1,
                beatmapId = 2,
                beatmapSetId = 3,
                threadId = 4,
                rankedStatus = RankedStatus.Qualified,
                osuRank = Rank.S,
                fruitsRank = Rank.A,
                taikoRank = Rank.B,
                maniaRank = Rank.C,
                checksum = "abc",
            )

        protocol.assertPacket(
            "4500001c00000001000000010002000000030000000400000003030405060b03616263",
            protocol.encode(BeatmapInfoReplyPacket(listOf(beatmap))),
        )
    }
}
