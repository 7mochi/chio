package io.github.nanamochi.chio.packet.types

enum class ReplayAction(val value: Int) {
    Standard(0),
    NewSong(1),
    Skip(2),
    Completion(3),
    Fail(4),
    Pause(5),
    Unpause(6),
    SongSelect(7),
    WatchingOther(8);

    companion object {
        private val byValue = entries.associateBy(ReplayAction::value)

        @JvmStatic
        fun from(value: Int): ReplayAction =
            byValue[value] ?: throw IllegalArgumentException("Unknown ReplayAction value: $value")
    }
}

enum class QuitState(val value: Int) {
    Gone(0),
    OsuRemaining(1),
    IrcRemaining(2);

    companion object {
        private val byValue = entries.associateBy(QuitState::value)

        @JvmStatic
        fun from(value: Int): QuitState =
            byValue[value] ?: throw IllegalArgumentException("Unknown QuitState value: $value")
    }
}

enum class Completeness(val value: Int) {
    StatusOnly(0),
    Statistics(1),
    Full(2);

    companion object {
        private val byValue = entries.associateBy(Completeness::value)

        @JvmStatic
        fun from(value: Int): Completeness =
            byValue[value] ?: throw IllegalArgumentException("Unknown Completeness value: $value")
    }
}
