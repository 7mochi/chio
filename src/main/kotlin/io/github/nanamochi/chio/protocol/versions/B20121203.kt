package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV20121203

/** b20121203 optimizes the way how the user presence is sent to the client. */
val B20121203: Protocol =
    B20121028.derive(20121203) {
        protocolVersion = 13
        encode(
            PacketEncoder<UserPresencePacket> { _, packet ->
                encodeUserPresenceV20121203(packet)
            }
        )
    }
