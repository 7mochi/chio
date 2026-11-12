package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.types.Rank
import io.github.nanamochi.chio.packet.types.RankedStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B388Test {
    private val protocol = Protocols.select(388)

    @Test
    fun `beatmap info reply maps ranked status`() {
        protocol.assertPacket(
            "46000076000000060000000000010000000000000000000000ff090b01610100020000000a0000006400000000090b016202000300000014000000c800000001090b01630300040000001e0000002c01000002090b0164040005000000280000009001000002090b016505000600000032000000f401000002090b0166",
            protocol.encode(BeatmapInfoReplyPacket(beatmapsV388())),
        )
    }

    @Test
    fun `b374 writes ranked state as boolean`() {
        val older = Protocols.select(374)

        older.assertPacket(
            "4600007600000006000000000001000000000000000000000000090b01610100020000000a0000006400000000090b016202000300000014000000c800000001090b01630300040000001e0000002c01000001090b0164040005000000280000009001000001090b016505000600000032000000f401000001090b0166",
            older.encode(BeatmapInfoReplyPacket(beatmapsV388())),
        )
    }
}

private fun beatmapsV388(): List<BeatmapInfo> =
    listOf(
            RankedStatus.NotSubmitted,
            RankedStatus.Pending,
            RankedStatus.Ranked,
            RankedStatus.Approved,
            RankedStatus.Qualified,
            RankedStatus.Loved,
        )
        .mapIndexed { index, status ->
            BeatmapInfo(
                index = index,
                beatmapId = index + 1,
                beatmapSetId = 10 * index,
                threadId = 100 * index,
                rankedStatus = status,
                osuRank = Rank.N,
                checksum = "abcdef"[index].toString(),
            )
        }
