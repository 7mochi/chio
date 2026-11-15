package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.SilenceInfoPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/**
 * b20121023 adds the "ban/silence info" packet, which locks down account switching on the
 * client-side, and tells the user how long they are banned/silenced for.
 */
val B20121023: Protocol =
    B20120818.derive(20121023) {
        protocolVersion = 11
        encode<SilenceInfoPacket>(PacketType.BanchoSilenceInfo) { writeInt(it.lengthSeconds) }
    }
