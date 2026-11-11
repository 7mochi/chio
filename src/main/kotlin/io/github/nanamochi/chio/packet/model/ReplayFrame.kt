package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.ButtonState

data class ReplayFrame
@JvmOverloads
constructor(
    val buttonState: ButtonState = ButtonState.NoButton,
    val mouseX: Float = 0f,
    val mouseY: Float = 0f,
    val time: Int = 0,
)
