package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV1183

/** b1183 adds the longitude and latitude fields to user presence. */
val B1183: Protocol =
    B695.derive(1183) {
        protocolVersion = 5
        encode(
            PacketEncoder<UserPresencePacket> { protocol, packet ->
                encodeUserPresenceV1183(protocol, packet)
            }
        )
    }
