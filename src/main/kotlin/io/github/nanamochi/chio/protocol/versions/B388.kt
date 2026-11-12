package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeBeatmapInfoReplyV388

/** b388 encodes the ranked status as a signed byte in beatmap info replies. */
val B388: Protocol =
    B374.derive(388) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
        writeBeatmapInfoReplyV388(it.beatmaps)
    }
}
