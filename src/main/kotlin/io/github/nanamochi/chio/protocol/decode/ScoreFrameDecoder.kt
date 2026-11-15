package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.ScoreFrame

private fun readScoreFrameBody(reader: BinaryReader, time: Int): ScoreFrame =
    ScoreFrame(
        time = time,
        id = reader.readUByte(),
        total300 = reader.readUShort(),
        total100 = reader.readUShort(),
        total50 = reader.readUShort(),
        totalGeki = reader.readUShort(),
        totalKatu = reader.readUShort(),
        totalMiss = reader.readUShort(),
        totalScore = reader.readUInt().toInt(),
        maxCombo = reader.readUShort(),
        currentCombo = reader.readUShort(),
        perfect = reader.readBoolean(),
        hp = reader.readUByte(),
    )

fun readScoreFrameV294(reader: BinaryReader): ScoreFrame {
    reader.readString()
    return readScoreFrameBody(reader, time = 0)
}

fun readScoreFrameV296(reader: BinaryReader): ScoreFrame {
    reader.readString()
    return readScoreFrameBody(reader, reader.readInt())
}

fun readScoreFrameV334(reader: BinaryReader): ScoreFrame =
    readScoreFrameBody(reader, reader.readInt())

fun readScoreFrameV535(reader: BinaryReader): ScoreFrame {
    val frame = readScoreFrameBody(reader, reader.readInt())
    return if (reader.hasRemaining()) frame.copy(tagByte = reader.readUByte()) else frame
}

fun readScoreFrameV20151106(reader: BinaryReader): ScoreFrame {
    val frame = readScoreFrameV535(reader)
    val usingScoreV2 = reader.readBoolean()
    return if (usingScoreV2) {
        frame.copy(
            usingScoreV2 = true,
            comboPortion = reader.readDouble(),
            bonusPortion = reader.readDouble(),
        )
    } else {
        frame
    }
}
