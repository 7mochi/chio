package io.github.nanamochi.chio.packet.model

data class UserStats
@JvmOverloads
constructor(
    val rank: Int = 0,
    val rankedScore: Long = 0,
    val totalScore: Long = 0,
    val accuracy: Float = 0f,
    val playcount: Int = 0,
    val performancePoints: Int = 0,
)
