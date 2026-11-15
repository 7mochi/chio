package io.github.nanamochi.chio.packet.model

data class Channel
@JvmOverloads
constructor(val name: String, val topic: String = "", val userCount: Int = 0)
