package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.types.Status

private fun statusToLegacyValue(status: UserStatus): Status =
    when {
        status.updateStats -> Status.StatsUpdate
        status.action.value > 9 -> Status.from(status.action.value - 1)
        else -> status.action
    }

fun BinaryWriter.writeStatusV282(status: UserStatus) {
    val action = statusToLegacyValue(status)
    writeUByte(action.value)
    if (action != Status.Unknown) {
        writeString(status.text)
        writeString(status.beatmapChecksum)
        writeUShort(status.mods.bits)
    }
}

fun BinaryWriter.writeStatusV338(status: UserStatus) {
    writeUByte(status.action.value)
    writeBoolean(true)
    writeString(status.text)
    writeString(status.beatmapChecksum)
    writeUShort(status.mods.bits)
}

fun BinaryWriter.writeStatusV489(status: UserStatus) {
    writeStatusV338(status)
    writeUByte(status.mode.value)
}

fun BinaryWriter.writeStatusV490(status: UserStatus) {
    writeStatusV489(status)
    writeInt(status.beatmapId)
}
