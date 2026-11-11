package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.types.SlotStatus

private fun BinaryWriter.writeMatchV298Layout(match: Match, includeInProgress: Boolean) {
    writeUByte(match.id)
    if (includeInProgress) writeBoolean(match.inProgress)
    writeUByte(match.type.value)
    writeString(match.name)
    writeString(match.beatmapText)
    writeInt(match.beatmapId)
    writeString(match.beatmapChecksum)
    writeBooleanList(match.slots.map { it.status == SlotStatus.Open })
    writeBooleanList(match.slots.map { it.hasPlayer })
    writeBooleanList(match.slots.map { it.status == SlotStatus.Ready })
    match.slots.filter { it.hasPlayer }.forEach { writeInt(it.userId) }
}

fun BinaryWriter.writeMatchV298(match: Match) {
    writeMatchV298Layout(match, includeInProgress = false)
}

fun BinaryWriter.writeMatchV312(match: Match) {
    writeMatchV298Layout(match, includeInProgress = true)
}

fun BinaryWriter.writeMatchV334(match: Match) {
    writeUByte(match.id)
    writeBoolean(match.inProgress)
    writeUByte(match.type.value)
    writeUShort(match.mods.bits)
    writeString(match.name)
    writeString(match.beatmapText)
    writeInt(match.beatmapId)
    writeString(match.beatmapChecksum)
    match.slots.forEach { writeUByte(it.status.bits) }
    match.slots.filter { it.hasPlayer }.forEach { writeInt(it.userId) }
}
