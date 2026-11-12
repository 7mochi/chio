package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV504
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV504
import io.github.nanamochi.chio.protocol.encode.writeBeatmapInfoReplyV504

/** b504 adds the taiko and fruits ranks and raises the total score cap. */
val B504: Protocol =
    B490.derive(504) {
        protocolVersion = 2
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
        writeBeatmapInfoReplyV504(it.beatmaps)
    }
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV504(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV504(protocol, packet)
        }
    )
}
