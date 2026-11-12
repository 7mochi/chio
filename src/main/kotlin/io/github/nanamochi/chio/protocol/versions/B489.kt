package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readUserStatusV489
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV489
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV489

/** b489 adds the gamemode to status updates. */
val B489: Protocol =
    B487.derive(489) {
        protocolVersion = 1
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuUserStatus) { reader -> UserStatusPacket(readUserStatusV489(reader)) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV489(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV489(protocol, packet)
        }
    )
}
