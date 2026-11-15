package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.ReplayAction

data class ReplayFrameBundle
@JvmOverloads
constructor(
    val action: ReplayAction = ReplayAction.Standard,
    val frames: List<ReplayFrame> = emptyList(),
    val frame: ScoreFrame? = null,
    val extra: Int = -1,
    val sequence: Int = 0,
)
