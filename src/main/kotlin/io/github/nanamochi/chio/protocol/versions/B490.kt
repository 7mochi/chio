package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readBeatmapInfoRequestV490
import io.github.nanamochi.chio.protocol.decode.readUserStatusV490
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV490
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV490

/** b490 adds the beatmap id to status updates and ids to beatmap info requests. */
val B490: Protocol =
    B489.derive(490) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuUserStatus) { reader -> UserStatusPacket(readUserStatusV490(reader)) }
    decode(PacketType.OsuBeatmapInfoRequest) { reader -> readBeatmapInfoRequestV490(reader) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV490(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV490(protocol, packet)
        }
    )
}
