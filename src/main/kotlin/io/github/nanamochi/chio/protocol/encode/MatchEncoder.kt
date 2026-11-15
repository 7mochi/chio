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

fun BinaryWriter.writeMatchV470(match: Match, slotSize: Int) {
    writeMatchV334Layout(match, slotSize, includeHost = true, includeMode = true)
}

fun BinaryWriter.writeMatchV535(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeScoringAndTeam = true,
    )
}

fun BinaryWriter.writeMatchV558(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includeScoringAndTeam = true,
    )
}

fun BinaryWriter.writeMatchV591(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
    )
}

fun BinaryWriter.writeMatchV1796(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        idAsU16 = true,
    )
}

fun BinaryWriter.writeMatchV20120818(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        idAsU16 = true,
        modsAsU32 = true,
    )
}

fun BinaryWriter.writeMatchV20130118(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        idAsU16 = true,
        modsAsU32 = true,
        includeFreemod = true,
    )
}

fun BinaryWriter.writeMatchV20130303(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        idAsU16 = true,
        modsAsU32 = true,
        includeFreemod = true,
        includeSlotMods = true,
    )
}

fun BinaryWriter.writeMatchV20130418(match: Match, slotSize: Int) {
    writeMatchV334Layout(
        match,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        idAsU16 = true,
        modsAsU32 = true,
        includeFreemod = true,
        includeSlotMods = true,
        includeSeed = true,
    )
}

private fun BinaryWriter.writeMatchV334Layout(
    match: Match,
    slotSize: Int,
    includeHost: Boolean,
    includeMode: Boolean = false,
    includeSlotTeams: Boolean = false,
    includePassword: Boolean = false,
    includeScoringAndTeam: Boolean = false,
    idAsU16: Boolean = false,
    modsAsU32: Boolean = false,
    includeFreemod: Boolean = false,
    includeSlotMods: Boolean = false,
    includeSeed: Boolean = false,
) {
    val slots = match.slotsForProtocol(slotSize)

    if (idAsU16) writeUShort(match.id) else writeUByte(match.id)
    writeBoolean(match.inProgress)
    writeUByte(match.type.value)
    if (modsAsU32) writeUInt(match.mods.bits) else writeUShort(match.mods.bits)
    writeString(match.name)
    if (includePassword) writeString(match.password)
    writeString(match.beatmapText)
    writeInt(match.beatmapId)
    writeString(match.beatmapChecksum)
    slots.forEach { writeUByte(it.status.bits) }
    if (includeSlotTeams) {
        slots.forEach { writeUByte(it.team.value) }
    }
    slots.filter { it.hasPlayer }.forEach { writeInt(it.userId) }
    if (includeHost) writeInt(match.hostId)
    if (includeMode) writeUByte(match.mode.value)
    if (includeScoringAndTeam) {
        writeUByte(match.scoringType.value)
        writeUByte(match.teamType.value)
    }
    if (includeFreemod) writeBoolean(match.freemod)
    if (includeSlotMods && match.freemod) {
        slots.forEach { writeUInt(it.mods.bits) }
    }
    if (includeSeed) writeUInt(match.seed)
}
