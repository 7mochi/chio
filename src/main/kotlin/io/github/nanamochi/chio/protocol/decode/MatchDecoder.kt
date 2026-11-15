package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.ScoringType
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam
import io.github.nanamochi.chio.packet.types.TeamType

private fun readMatchV298Layout(
    reader: BinaryReader,
    slotSize: Int,
    id: Int,
    inProgress: Boolean,
): Match {
    val type = MatchType.from(reader.readUByte())
    val name = reader.readString()
    val beatmapText = reader.readString()
    val beatmapId = reader.readInt()
    val beatmapChecksum = reader.readString()

    val slotsOpen = reader.readBooleanList(slotSize)
    val slotsUsed = reader.readBooleanList(slotSize)
    val slotsReady = reader.readBooleanList(slotSize)

    val slots =
        List(slotSize) { index ->
            var status = if (slotsOpen[index]) SlotStatus.Open else SlotStatus.Locked
            if (slotsUsed[index]) status = SlotStatus.NotReady
            if (slotsReady[index]) status = SlotStatus.Ready

            reader.readMatchSlot(status)
        }

    return Match(
        id = id,
        inProgress = inProgress,
        type = type,
        name = name,
        beatmapText = beatmapText,
        beatmapId = beatmapId,
        beatmapChecksum = beatmapChecksum,
        slots = slots,
    )
}

fun readMatchV298(reader: BinaryReader, slotSize: Int): Match {
    val id = reader.readUByte()
    return readMatchV298Layout(reader, slotSize, id, inProgress = false)
}

fun readMatchV312(reader: BinaryReader, slotSize: Int): Match {
    val id = reader.readUByte()
    val inProgress = reader.readBoolean()
    return readMatchV298Layout(reader, slotSize, id, inProgress)
}

fun readMatchV334(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(reader, slotSize, includeHost = false)

fun readMatchV402(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(reader, slotSize, includeHost = true)

fun readMatchV470(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(reader, slotSize, includeHost = true, includeMode = true)

fun readMatchV535(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeScoringAndTeam = true,
    )

fun readMatchV558(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
    )

fun readMatchV591(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
    )

fun readMatchV1796(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
        idAsU16 = true,
    )

fun readMatchV20120818(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
        idAsU16 = true,
        modsAsU32 = true,
    )

fun readMatchV20130118(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
        idAsU16 = true,
        modsAsU32 = true,
        includeFreemod = true,
    )

fun readMatchV20130303(reader: BinaryReader, slotSize: Int): Match =
    readMatchV334Layout(
        reader,
        slotSize,
        includeHost = true,
        includeMode = true,
        includeSlotTeams = true,
        includePassword = true,
        includeScoringAndTeam = true,
        optionalScoringAndTeam = false,
        idAsU16 = true,
        modsAsU32 = true,
        includeFreemod = true,
        includeSlotMods = true,
    )

private fun readMatchV334Layout(
    reader: BinaryReader,
    slotSize: Int,
    includeHost: Boolean,
    includeMode: Boolean = false,
    includeSlotTeams: Boolean = false,
    includePassword: Boolean = false,
    includeScoringAndTeam: Boolean = false,
    optionalScoringAndTeam: Boolean = true,
    idAsU16: Boolean = false,
    modsAsU32: Boolean = false,
    includeFreemod: Boolean = false,
    includeSlotMods: Boolean = false,
): Match {
    val id = if (idAsU16) reader.readUShort() else reader.readUByte()
    val inProgress = reader.readBoolean()
    val type = MatchType.from(reader.readUByte())
    val mods = Mods.fromBits(if (modsAsU32) reader.readInt() else reader.readUShort())
    val name = reader.readString()
    val password = if (includePassword) reader.readString() else ""
    val beatmapText = reader.readString()
    val beatmapId = reader.readInt()
    val beatmapChecksum = reader.readString()

    val statuses = List(slotSize) { SlotStatus(reader.readUByte()) }
    val teams =
        if (includeSlotTeams) {
            List(slotSize) { SlotTeam.from(reader.readUByte()) }
        } else {
            List(slotSize) { SlotTeam.Neutral }
        }
    val slots = statuses.mapIndexed { index, status -> reader.readMatchSlot(status, teams[index]) }
    val hostId = if (includeHost) reader.readInt() else -1
    val mode = if (includeMode) Mode.from(reader.readUByte()) else Mode.Osu
    var scoringType = ScoringType.Score
    var teamType = TeamType.HeadToHead
    if (includeScoringAndTeam) {
        if (!optionalScoringAndTeam || reader.hasRemaining()) {
            scoringType = ScoringType.from(reader.readUByte())
        }
        if (!optionalScoringAndTeam || reader.hasRemaining()) {
            teamType = TeamType.from(reader.readUByte())
        }
    }
    val freemod = if (includeFreemod) reader.readBoolean() else false
    val slotsWithMods =
        if (includeSlotMods && freemod) {
            slots.map { it.copy(mods = Mods.fromBits(reader.readInt())) }
        } else {
            slots
        }

    return Match(
        id = id,
        inProgress = inProgress,
        type = type,
        mods = mods,
        name = name,
        password = password,
        beatmapText = beatmapText,
        beatmapId = beatmapId,
        beatmapChecksum = beatmapChecksum,
        slots = slotsWithMods,
        hostId = hostId,
        mode = mode,
        scoringType = scoringType,
        teamType = teamType,
        freemod = freemod,
    )
}

private fun BinaryReader.readMatchSlot(
    status: SlotStatus,
    team: SlotTeam = SlotTeam.Neutral,
): MatchSlot {
    val userId = if (status.hasPlayer()) readInt() else -1

    return MatchSlot(userId = userId, status = status, team = team)
}
