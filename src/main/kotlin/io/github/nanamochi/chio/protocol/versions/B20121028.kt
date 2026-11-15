package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeBeatmapInfoReplyV20121028

/** b20121028 adds the osu! mania ranking field to the beatmap info packet. */
val B20121028: Protocol =
    B20121023.derive(20121028) {
        protocolVersion = 12
        encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
            writeBeatmapInfoReplyV20121028(it.beatmaps)
        }
    }
