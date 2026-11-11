package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Status

data class UserStatus
@JvmOverloads
constructor(
    val action: Status = Status.Idle,
    val text: String = "",
    val mods: Mods = Mods.NoMod,
    val mode: Mode = Mode.Osu,
    val beatmapChecksum: String = "",
    val beatmapId: Int = -1,
    val updateStats: Boolean = false,
)
