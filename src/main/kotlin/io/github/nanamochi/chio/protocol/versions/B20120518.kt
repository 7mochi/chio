package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.ChannelInfoCompletePacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20120518 introduced the [ChannelInfoCompletePacket] packet. */
val B20120518: Protocol =
    B1820.derive(20120518) {
        encodeEmpty<ChannelInfoCompletePacket>(PacketType.BanchoChannelInfoComplete)
    }
