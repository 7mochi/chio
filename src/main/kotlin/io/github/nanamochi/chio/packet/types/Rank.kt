package io.github.nanamochi.chio.packet.types

enum class Rank(val value: Int) {
    XH(0),
    SH(1),
    X(2),
    S(3),
    A(4),
    B(5),
    C(6),
    D(7),
    F(8),
    N(9);

    companion object {
        private val byValue = entries.associateBy(Rank::value)

        @JvmStatic
        fun from(value: Int): Rank =
            byValue[value] ?: throw IllegalArgumentException("Unknown Rank value: $value")
    }
}
