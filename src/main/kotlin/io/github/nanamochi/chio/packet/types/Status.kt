package io.github.nanamochi.chio.packet.types

enum class Status(val value: Int) {
    Idle(0),
    Afk(1),
    Playing(2),
    Editing(3),
    Modding(4),
    Multiplayer(5),
    Watching(6),
    Unknown(7),
    Testing(8),
    Submitting(9),
    Paused(10),
    Lobby(11),
    Multiplaying(12),
    OsuDirect(13),

    /** Alias used by older clients to force a stats update. Shares value 10 with [Paused]. */
    StatsUpdate(10);

    companion object {
        // The alias shares value 10 with Paused. Decoding must return Paused so the alias never
        // leaks.
        private val byValue = entries.filterNot { it == StatsUpdate }.associateBy(Status::value)

        @JvmStatic
        fun from(value: Int): Status =
            byValue[value] ?: throw IllegalArgumentException("Unknown Status value: $value")
    }
}
