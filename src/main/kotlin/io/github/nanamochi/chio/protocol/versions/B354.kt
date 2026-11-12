package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readBeatmapInfoRequestV354
import io.github.nanamochi.chio.protocol.derive

/** b354 adds the beatmap info request and reply packets. */
val B354: Protocol =
    B349.derive(354) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuBeatmapInfoRequest) { reader ->
        readBeatmapInfoRequestV354(reader)
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
        writeUInt(it.beatmaps.size)
        it.beatmaps.forEach { info ->
            writeShort(info.index)
            writeInt(info.beatmapId)
            writeInt(info.beatmapSetId)
            writeInt(info.threadId)
            writeBoolean(info.isRanked)
            writeByte(info.osuRank.value)
            writeString(info.checksum)
        }
    }
}
