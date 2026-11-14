package io.github.nanamochi.chio.packet.types

enum class AvatarExtension(val value: Int) {
    Empty(0),
    Png(1),
    Jpg(2);

    companion object {
        private val byValue = entries.associateBy(AvatarExtension::value)

        @JvmStatic
        fun from(value: Int): AvatarExtension =
            byValue[value]
                ?: throw IllegalArgumentException("Unknown AvatarExtension value: $value")
    }
}
