package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV695
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV695

/** b695 changes the rank datatype from u16 to s32. */
val B695: Protocol =
    B659.derive(695) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV695(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV695(protocol, packet)
        }
    )
}
