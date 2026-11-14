package io.github.nanamochi.chio.packet.types

enum class PresenceFilter(val value: Int) {
    NoPlayers(0),
    All(1),
    Friends(2);

    companion object {
        private val byValue = entries.associateBy(PresenceFilter::value)

        @JvmStatic
        fun from(value: Int): PresenceFilter =
            byValue[value] ?: throw IllegalArgumentException("Unknown PresenceFilter value: $value")
    }
}
