package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.Channel

fun BinaryWriter.writeChannelAvailableV20120725(channel: Channel) {
    writeString(channel.name)
    writeString(channel.topic)
    writeShort(channel.userCount)
}
