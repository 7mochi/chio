package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV20120806

/** b20120806 adds the current user's mode to the presence packet. */
val B20120806: Protocol =
    B20120725.derive(20120806) {
        protocolVersion = 10
        encode(
            PacketEncoder<UserPresencePacket> { _, packet ->
                encodeUserPresenceV20120806(packet)
            }
        )
    }
