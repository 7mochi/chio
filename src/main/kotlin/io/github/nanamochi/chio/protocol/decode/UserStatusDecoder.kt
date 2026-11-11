package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Status

private fun statusFromLegacyValue(value: Int): Status =
    when {
        value == 10 -> Status.Unknown
        value > 9 -> Status.from(value - 1)
        else -> Status.from(value)
    }

fun readUserStatusV282(reader: BinaryReader): UserStatus {
    var action = statusFromLegacyValue(reader.readUByte())
    var text = ""
    var checksum = ""
    var mods = Mods.NoMod

    if (action != Status.Unknown) {
        text = reader.readString()
        checksum = reader.readString()
        mods = Mods.fromBits(reader.readUShort())
    }

    // The client starts playing but doesn't always update the action.
    if (action == Status.Idle && checksum.isNotEmpty()) {
        action = Status.Playing
    }

    return UserStatus(action = action, text = text, mods = mods, beatmapChecksum = checksum)
}

fun readUserStatusV338(reader: BinaryReader): UserStatus {
    var action = Status.from(reader.readUByte())
    var text = ""
    var checksum = ""
    var mods = Mods.NoMod

    if (reader.readBoolean()) {
        text = reader.readString()
        checksum = reader.readString()
        mods = Mods.fromBits(reader.readUShort())
    }

    if (action == Status.Idle && checksum.isNotEmpty()) {
        action = Status.Playing
    }

    return UserStatus(action = action, text = text, mods = mods, beatmapChecksum = checksum)
}
