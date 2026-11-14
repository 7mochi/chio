package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.ChannelLeavePacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b613 allows the client to leave channels. */
val B613: Protocol =
    B591.derive(613) {
        decode(PacketType.OsuChannelLeave) { reader -> ChannelLeavePacket(reader.readString()) }
    }
