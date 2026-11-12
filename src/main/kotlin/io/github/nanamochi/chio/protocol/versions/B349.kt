package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.ChannelJoinPacket
import io.github.nanamochi.chio.packet.outbound.ChannelAvailableAutojoinPacket
import io.github.nanamochi.chio.packet.outbound.ChannelAvailablePacket
import io.github.nanamochi.chio.packet.outbound.ChannelJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.ChannelRevokedPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b349 adds the Channel model and the channel join, revocation and availability packets. */
val B349: Protocol =
    B342.derive(349) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuChannelJoin) { reader -> ChannelJoinPacket(reader.readString()) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode<ChannelJoinSuccessPacket>(PacketType.BanchoChannelJoinSuccess) {
        writeString(it.channel)
    }
    encode<ChannelAvailablePacket>(PacketType.BanchoChannelAvailable) {
        writeString(it.channel.name)
    }
    encode<ChannelRevokedPacket>(PacketType.BanchoChannelRevoked) { writeString(it.channel) }
    encode<ChannelAvailableAutojoinPacket>(PacketType.BanchoChannelAvailableAutojoin) {
        writeString(it.channel.name)
    }
}
