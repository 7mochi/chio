package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.cappedTotalScore
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV338
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV338

/** b365 caps the total score in user stats and presence to avoid a client crash. */
val B365: Protocol =
    B354.derive(365) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV338(protocol, packet.copy(info = packet.info.cappedTotalScore()))
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV338(protocol, packet.copy(info = packet.info.cappedTotalScore()))
        }
    )
}
