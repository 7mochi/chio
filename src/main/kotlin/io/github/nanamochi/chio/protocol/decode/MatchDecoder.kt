package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.types.MatchType
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.SlotStatus

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

private fun readMatchV334Layout(
    reader: BinaryReader,
    slotSize: Int,
    includeHost: Boolean,
    includeMode: Boolean = false,
): Match {
    val id = reader.readUByte()
    val inProgress = reader.readBoolean()
    val type = MatchType.from(reader.readUByte())
    val mods = Mods.fromBits(reader.readUShort())
    val name = reader.readString()
    val beatmapText = reader.readString()
    val beatmapId = reader.readInt()
    val beatmapChecksum = reader.readString()

    val statuses = List(slotSize) { SlotStatus(reader.readUByte()) }
    val slots = statuses.map { reader.readMatchSlot(it) }
    val hostId = if (includeHost) reader.readInt() else -1
    val mode = if (includeMode) Mode.from(reader.readUByte()) else Mode.Osu

    return Match(
        id = id,
        inProgress = inProgress,
        type = type,
        mods = mods,
        name = name,
        beatmapText = beatmapText,
        beatmapId = beatmapId,
        beatmapChecksum = beatmapChecksum,
        slots = slots,
        hostId = hostId,
        mode = mode,
    )
}

private fun BinaryReader.readMatchSlot(status: SlotStatus): MatchSlot {
    val userId = if (status.hasPlayer()) readInt() else -1

    return MatchSlot(userId = userId, status = status)
}
