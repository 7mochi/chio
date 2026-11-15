package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.UserSilencedPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20121212 adds support for the user silenced packet and changes the chat link format. */
val B20121212: Protocol =
    B20121211.derive(20121212) {
        encode<UserSilencedPacket>(PacketType.BanchoUserSilenced) { writeInt(it.userId) }
    }
