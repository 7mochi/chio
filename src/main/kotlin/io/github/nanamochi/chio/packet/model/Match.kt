package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.ScoringType
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.TeamType

data class Match
@JvmOverloads
constructor(
    val id: Int = 0,
    val inProgress: Boolean = false,
    val type: MatchType = MatchType.Standard,
    val mods: Mods = Mods.NoMod,
    val name: String = "",
    val password: String = "",
    val beatmapText: String = "",
    val beatmapId: Int = -1,
    val beatmapChecksum: String = "",
    val slots: List<MatchSlot> = emptyList(),
    val hostId: Int = -1,
    val mode: Mode = Mode.Osu,
    val scoringType: ScoringType = ScoringType.Score,
    val teamType: TeamType = TeamType.HeadToHead,
    val freemod: Boolean = false,
    val seed: Int = 0,
) {
    /**
     * Adds locked slots until the match has at least [slotSize] slots.
     *
     * Extra slots are left untouched. Use this before writing when the match may have fewer slots
     * than the protocol expects.
     */
    fun withMinimumSlotCount(slotSize: Int): Match =
        if (slots.size >= slotSize) {
            this
        } else {
            copy(
                slots =
                    slots + List(slotSize - slots.size) { MatchSlot(status = SlotStatus.Locked) }
            )
        }
}
