package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.types.RankedStatus
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b388 encodes the ranked status as a signed byte in beatmap info replies. */
val B388: Protocol =
    B374.derive(388) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode<BeatmapInfoReplyPacket>(PacketType.BanchoBeatmapInfoReply) {
        writeUInt(it.beatmaps.size)
        it.beatmaps.forEach { info ->
            writeShort(info.index)
            writeInt(info.beatmapId)
            writeInt(info.beatmapSetId)
            writeInt(info.threadId)
            writeByte(info.rankedStatus.beatmapInfoValue())
            writeByte(info.osuRank.value)
            writeString(info.checksum)
        }
    }
}

private fun RankedStatus.beatmapInfoValue(): Int =
    when (this) {
        RankedStatus.NotSubmitted -> -1
        RankedStatus.Pending -> 0
        RankedStatus.Ranked -> 1
        RankedStatus.Approved,
        RankedStatus.Qualified,
        RankedStatus.Loved -> 2
    }
