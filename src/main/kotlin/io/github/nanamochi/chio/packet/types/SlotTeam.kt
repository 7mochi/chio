package io.github.nanamochi.chio.packet.types

enum class SlotTeam(val value: Int) {
    Neutral(0),
    Blue(1),
    Red(2);

    /** Blue and Red swap. Neutral falls back to Red because it has no opposing team. */
    val opposite: SlotTeam
        get() = if (this == Red) Blue else Red

    companion object {
        private val byValue = entries.associateBy(SlotTeam::value)

        @JvmStatic
        fun from(value: Int): SlotTeam =
            byValue[value] ?: throw IllegalArgumentException("Unknown SlotTeam value: $value")
    }
}
