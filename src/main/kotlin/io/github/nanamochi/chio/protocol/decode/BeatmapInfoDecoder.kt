package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.inbound.BeatmapInfoRequestPacket

private fun BinaryReader.readStringList32(): List<String> {
    val count = readUInt()
    require(count <= Int.MAX_VALUE) { "List length $count is too large" }
    return List(count.toInt()) { readString() }
}

fun readBeatmapInfoRequestV354(reader: BinaryReader): BeatmapInfoRequestPacket =
    BeatmapInfoRequestPacket(filenames = reader.readStringList32())

fun readBeatmapInfoRequestV490(reader: BinaryReader): BeatmapInfoRequestPacket =
    BeatmapInfoRequestPacket(
        filenames = reader.readStringList32(),
        ids = reader.readIntList32().toList(),
    )
