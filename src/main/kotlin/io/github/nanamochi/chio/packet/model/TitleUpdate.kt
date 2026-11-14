package io.github.nanamochi.chio.packet.model

/** Represents a title update, i.e. the image at the bottom of the main menu. */
data class TitleUpdate @JvmOverloads constructor(val imageUrl: String = "")
