package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV20120723

/** b20120723 now shows your performance points in-game. */
val B20120723: Protocol =
    B20120703.derive(20120723) {
        protocolVersion = 8
        encode(
            PacketEncoder<UserStatsPacket> { _, packet ->
                encodeUserStatsV20120723(packet)
            }
        )
    }
