package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.types.SlotStatus

private fun Match.slotsForProtocol(slotSize: Int): List<MatchSlot> =
    copy(slots = slots.take(slotSize)).withMinimumSlotCount(slotSize).slots

private fun BinaryWriter.writeMatchV298Layout(
    match: Match,
    slotSize: Int,
    includeInProgress: Boolean,
) {
    val slots = match.slotsForProtocol(slotSize)

    writeUByte(match.id)
    if (includeInProgress) writeBoolean(match.inProgress)
    writeUByte(match.type.value)
    writeString(match.name)
    writeString(match.beatmapText)
    writeInt(match.beatmapId)
    writeString(match.beatmapChecksum)
    writeBooleanList(slots.map { it.status == SlotStatus.Open })
    writeBooleanList(slots.map { it.hasPlayer })
    writeBooleanList(slots.map { it.status == SlotStatus.Ready })
    slots.filter { it.hasPlayer }.forEach { writeInt(it.userId) }
}

fun BinaryWriter.writeMatchV298(match: Match, slotSize: Int) {
    writeMatchV298Layout(match, slotSize, includeInProgress = false)
}

fun BinaryWriter.writeMatchV312(match: Match, slotSize: Int) {
    writeMatchV298Layout(match, slotSize, includeInProgress = true)
}

fun BinaryWriter.writeMatchV334(match: Match, slotSize: Int) {
    writeMatchV334Layout(match, slotSize, includeHost = false)
}

fun BinaryWriter.writeMatchV402(match: Match, slotSize: Int) {
    writeMatchV334Layout(match, slotSize, includeHost = true)
}

private fun BinaryWriter.writeMatchV334Layout(
    match: Match,
    slotSize: Int,
    includeHost: Boolean,
) {
    val slots = match.slotsForProtocol(slotSize)

    writeUByte(match.id)
    writeBoolean(match.inProgress)
    writeUByte(match.type.value)
    writeUShort(match.mods.bits)
    writeString(match.name)
    writeString(match.beatmapText)
    writeInt(match.beatmapId)
    writeString(match.beatmapChecksum)
    slots.forEach { writeUByte(it.status.bits) }
    slots.filter { it.hasPlayer }.forEach { writeInt(it.userId) }
    if (includeHost) writeInt(match.hostId)
}
