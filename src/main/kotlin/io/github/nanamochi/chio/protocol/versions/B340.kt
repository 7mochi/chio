package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchHasBeatmapPacket
import io.github.nanamochi.chio.protocol.PacketIdMappingV340
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b340 removes MatchChangeBeatmap and adds MatchHasBeatmap. */
val B340: Protocol =
    B338.derive(340) {
        idMapping = PacketIdMappingV340
        registerInbound()
    }

private fun ProtocolBuilder.registerInbound() {
    removeDecoder(PacketType.OsuMatchChangeBeatmap)
    decode(PacketType.OsuMatchHasBeatmap) { _ -> MatchHasBeatmapPacket }
}
