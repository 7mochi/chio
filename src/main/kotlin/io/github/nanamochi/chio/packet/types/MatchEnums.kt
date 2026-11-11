package io.github.nanamochi.chio.packet.types

enum class MatchType(val value: Int) {
    Standard(0),
    Powerplay(1);

    companion object {
        private val byValue = entries.associateBy(MatchType::value)

        @JvmStatic
        fun from(value: Int): MatchType =
            byValue[value] ?: throw IllegalArgumentException("Unknown MatchType value: $value")
    }
}

enum class ScoringType(val value: Int) {
    Score(0),
    Accuracy(1),
    Combo(2),
    ScoreV2(3);

    companion object {
        private val byValue = entries.associateBy(ScoringType::value)

        @JvmStatic
        fun from(value: Int): ScoringType =
            byValue[value] ?: throw IllegalArgumentException("Unknown ScoringType value: $value")
    }
}

enum class TeamType(val value: Int) {
    HeadToHead(0),
    TagCoop(1),
    TeamVs(2),
    TagTeamVs(3);

    companion object {
        private val byValue = entries.associateBy(TeamType::value)

        @JvmStatic
        fun from(value: Int): TeamType =
            byValue[value] ?: throw IllegalArgumentException("Unknown TeamType value: $value")
    }
}
