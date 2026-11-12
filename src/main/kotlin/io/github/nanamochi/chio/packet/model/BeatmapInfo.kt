package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Rank
import io.github.nanamochi.chio.packet.types.RankedStatus

data class BeatmapInfo
@JvmOverloads
constructor(
    val index: Int = 0,
    val beatmapId: Int = 0,
    val beatmapSetId: Int = 0,
    val threadId: Int = 0,
    val rankedStatus: RankedStatus = RankedStatus.NotSubmitted,
    val osuRank: Rank = Rank.N,
    val checksum: String = "",
) {
    val isRanked: Boolean
        get() = rankedStatus.isRanked
}
