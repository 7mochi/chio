package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.types.RankedStatus

private fun RankedStatus.beatmapInfoValue(): Int =
    when (this) {
        RankedStatus.NotSubmitted -> -1
        RankedStatus.Pending -> 0
        RankedStatus.Ranked -> 1
        RankedStatus.Approved,
        RankedStatus.Qualified,
        RankedStatus.Loved -> 2
    }

private fun BinaryWriter.writeBeatmapInfoReplyLayout(
    beatmaps: List<BeatmapInfo>,
    includeExtraRanks: Boolean,
) {
    writeUInt(beatmaps.size)
    beatmaps.forEach { info ->
        writeShort(info.index)
        writeInt(info.beatmapId)
        writeInt(info.beatmapSetId)
        writeInt(info.threadId)
        writeByte(info.rankedStatus.beatmapInfoValue())
        writeByte(info.osuRank.value)
        if (includeExtraRanks) {
            writeByte(info.fruitsRank.value)
            writeByte(info.taikoRank.value)
        }
        writeString(info.checksum)
    }
}

fun BinaryWriter.writeBeatmapInfoReplyV388(beatmaps: List<BeatmapInfo>) {
    writeBeatmapInfoReplyLayout(beatmaps, includeExtraRanks = false)
}

fun BinaryWriter.writeBeatmapInfoReplyV504(beatmaps: List<BeatmapInfo>) {
    writeBeatmapInfoReplyLayout(beatmaps, includeExtraRanks = true)
}
