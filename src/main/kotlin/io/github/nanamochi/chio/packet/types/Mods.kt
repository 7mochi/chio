package io.github.nanamochi.chio.packet.types

/**
 * Mods applied to a play.
 *
 * More than one can be active at once. The protocol encodes the active set as a bitmask.
 */
@JvmInline
value class Mods(val bits: Int) {
    operator fun plus(other: Mods): Mods = Mods(bits or other.bits)

    companion object {
        val NoMod = Mods(0)
        val NoFail = Mods(1 shl 0)
        val Easy = Mods(1 shl 1)
        val NoVideo = Mods(1 shl 2)
        val Hidden = Mods(1 shl 3)
        val HardRock = Mods(1 shl 4)
        val SuddenDeath = Mods(1 shl 5)
        val DoubleTime = Mods(1 shl 6)
        val Relax = Mods(1 shl 7)
        val HalfTime = Mods(1 shl 8)
        val Nightcore = Mods(1 shl 9)
        val Flashlight = Mods(1 shl 10)
        val Autoplay = Mods(1 shl 11)
        val SpunOut = Mods(1 shl 12)
        val Autopilot = Mods(1 shl 13)
        val Perfect = Mods(1 shl 14)
        val Key4 = Mods(1 shl 15)
        val Key5 = Mods(1 shl 16)
        val Key6 = Mods(1 shl 17)
        val Key7 = Mods(1 shl 18)
        val Key8 = Mods(1 shl 19)
        val FadeIn = Mods(1 shl 20)
        val Random = Mods(1 shl 21)
        val Cinema = Mods(1 shl 22)
        val Target = Mods(1 shl 23)
        val Key9 = Mods(1 shl 24)
        val KeyCoop = Mods(1 shl 25)
        val Key1 = Mods(1 shl 26)
        val Key3 = Mods(1 shl 27)
        val Key2 = Mods(1 shl 28)
        val ScoreV2 = Mods(1 shl 29)
        val Mirror = Mods(1 shl 30)

        @JvmStatic fun fromBits(bits: Int): Mods = Mods(bits)
    }
}
