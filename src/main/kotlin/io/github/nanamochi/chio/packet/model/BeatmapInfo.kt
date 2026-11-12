package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Rank

data class BeatmapInfo
@JvmOverloads
constructor(
    val index: Int = 0,
    val beatmapId: Int = 0,
    val beatmapSetId: Int = 0,
    val threadId: Int = 0,
    val isRanked: Boolean = false,
    val osuRank: Rank = Rank.N,
    val checksum: String = "",
)
