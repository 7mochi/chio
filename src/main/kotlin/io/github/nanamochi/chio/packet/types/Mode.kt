package io.github.nanamochi.chio.packet.types

enum class Mode(val value: Int, val formatted: String, val alias: String) {
    Osu(0, "osu!", "osu"),
    Taiko(1, "Taiko", "taiko"),
    CatchTheBeat(2, "CatchTheBeat", "fruits"),
    OsuMania(3, "osu!mania", "mania");

    companion object {
        private val byValue = entries.associateBy(Mode::value)

        @JvmStatic
        fun from(value: Int): Mode =
            byValue[value] ?: throw IllegalArgumentException("Unknown Mode value: $value")

        /** Clients and console commands identify modes by short names such as `std` or `ctb`. */
        @JvmStatic
        fun fromAlias(alias: String): Mode? =
            when (alias) {
                "std",
                "osu" -> Osu
                "taiko" -> Taiko
                "fruits",
                "ctb",
                "catch" -> CatchTheBeat
                "mania" -> OsuMania
                else -> null
            }
    }
}
