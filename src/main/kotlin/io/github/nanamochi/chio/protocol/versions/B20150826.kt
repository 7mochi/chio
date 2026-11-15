package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeBeatmapInfoReplyV20150826

/** b20150826 adds the "Qualified" status to the beatmap info reply. */
val B20150826: Protocol =
    B20141104.derive(20150826) {
        encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
            writeBeatmapInfoReplyV20150826(it.beatmaps)
        }
    }
