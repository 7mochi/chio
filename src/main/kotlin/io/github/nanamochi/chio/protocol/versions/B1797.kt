package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV1797

/** b1797 adds the rank field to re-enable user rank sorting. */
val B1797: Protocol =
    B1796.derive(1797) {
        protocolVersion = 7
        encode(
            PacketEncoder<UserPresencePacket> { _, packet ->
                encodeUserPresenceV1797(packet)
            }
        )
    }
