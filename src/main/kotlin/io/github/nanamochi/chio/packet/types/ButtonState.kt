package io.github.nanamochi.chio.packet.types

/**
 * Buttons held during a replay frame.
 *
 * Left and right can be active together.
 */
@JvmInline
value class ButtonState(val bits: Int) {
    operator fun plus(other: ButtonState): ButtonState = ButtonState(bits or other.bits)

    operator fun contains(other: ButtonState): Boolean =
        other.bits == 0 || (bits and other.bits) == other.bits

    companion object {
        val NoButton = ButtonState(0)
        val Left1 = ButtonState(1 shl 0)
        val Right1 = ButtonState(1 shl 1)
        val Left2 = ButtonState(1 shl 2)
        val Right2 = ButtonState(1 shl 3)
        val Smoke = ButtonState(1 shl 4)
    }
}
