package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.ChannelAvailableAutojoinPacket
import io.github.nanamochi.chio.packet.outbound.ChannelAvailablePacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeChannelAvailableV20120725

/** b20120725 now displays the topic and user count of a channel in the channel listing. */
val B20120725: Protocol =
    B20120723.derive(20120725) {
        protocolVersion = 9
        encode<ChannelAvailablePacket>(PacketType.BanchoChannelAvailable) {
            writeChannelAvailableV20120725(it.channel)
        }
        encode<ChannelAvailableAutojoinPacket>(PacketType.BanchoChannelAvailableAutojoin) {
            writeChannelAvailableV20120725(it.channel)
        }
    }
