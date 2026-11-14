package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.SetIrcAwayMessagePacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.decode.readMessageV320
import io.github.nanamochi.chio.protocol.derive

/** b1600 adds `/away` commands. */
val B1600: Protocol =
    B1365.derive(1600) {
        decode(PacketType.OsuSetIrcAwayMessage) { reader ->
            SetIrcAwayMessagePacket(readMessageV320(reader))
        }
    }
