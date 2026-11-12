package io.github.nanamochi.chio.packet.types

enum class RankedStatus(val value: Int) {
    NotSubmitted(-1),
    Pending(0),
    Ranked(1),
    Approved(2),
    Qualified(3),
    Loved(4);

    val isRanked: Boolean
        get() = this == Ranked || this == Approved || this == Qualified || this == Loved

    companion object {
        private val byValue = entries.associateBy(RankedStatus::value)

        @JvmStatic
        fun from(value: Int): RankedStatus =
            byValue[value] ?: throw IllegalArgumentException("Unknown RankedStatus value: $value")
    }
}
