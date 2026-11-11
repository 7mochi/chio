package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam

data class MatchSlot
@JvmOverloads
constructor(
    val userId: Int = -1,
    val status: SlotStatus = SlotStatus.Open,
    val team: SlotTeam = SlotTeam.Neutral,
    val mods: Mods = Mods.NoMod,
) {
    val hasPlayer: Boolean
        get() = status.hasPlayer()
}
