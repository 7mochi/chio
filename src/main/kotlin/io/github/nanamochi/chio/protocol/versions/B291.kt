package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.AnnouncePacket
import io.github.nanamochi.chio.packet.outbound.GetAttentionPacket
import io.github.nanamochi.chio.packet.outbound.RestartPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b291 adds GetAttention, Announce, and Restart implemented as an announce. */
val B291: Protocol =
    B282.derive(291) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encodeEmpty<GetAttentionPacket>(PacketType.BanchoGetAttention)
    encode<AnnouncePacket>(PacketType.BanchoAnnounce) { writeString(it.message) }
    encode(
        PacketEncoder<RestartPacket> { protocol, _ ->
            protocol
                .encodedPackets(AnnouncePacket("Bancho is restarting, please wait..."))
                .orEmpty()
        }
    )
}
