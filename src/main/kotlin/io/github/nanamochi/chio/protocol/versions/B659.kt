package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV659
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV659

/** b659 fixes the level overflow bug from b365. */
val B659: Protocol =
    B634.derive(659) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV659(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV659(protocol, packet)
        }
    )
}
