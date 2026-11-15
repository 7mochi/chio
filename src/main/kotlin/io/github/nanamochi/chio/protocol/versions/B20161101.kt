package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeBeatmapInfoReplyV20161101

/** b20161101 introduced the loved status to the beatmap info reply. */
val B20161101: Protocol =
    B20160404.derive(20161101) {
        encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
            writeBeatmapInfoReplyV20161101(it.beatmaps)
        }
    }
