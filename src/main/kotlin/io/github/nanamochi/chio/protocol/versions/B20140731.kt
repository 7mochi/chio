package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.RTXPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20140731 adds the infamous "Zallius' eyes have awoken" (RTX) packet. */
val B20140731: Protocol =
    B20140716.derive(20140731) {
        encode<RTXPacket>(PacketType.BanchoRTX) { writeString(it.message) }
    }
