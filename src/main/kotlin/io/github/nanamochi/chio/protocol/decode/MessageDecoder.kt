package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.Message

fun readChannelMessageV282(reader: BinaryReader): Message =
    Message(sender = "", content = reader.readString(), target = "#osu")

fun readPrivateMessageV294(reader: BinaryReader): Message {
    val target = reader.readString()
    val content = reader.readString()
    val isDirectMessage = reader.readBoolean()

    require(isDirectMessage) { "Expected a direct message, got a channel message" }

    return Message(sender = "", content = content, target = target)
}

fun readMessageV320(reader: BinaryReader): Message =
    Message(
        sender = reader.readString(),
        content = reader.readString(),
        target = reader.readString(),
    )
