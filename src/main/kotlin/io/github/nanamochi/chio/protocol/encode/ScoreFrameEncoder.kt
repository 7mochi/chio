package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.ScoreFrame

private fun BinaryWriter.writeScoreFrameBody(frame: ScoreFrame) {
    writeUByte(frame.id)
    writeUShort(frame.total300)
    writeUShort(frame.total100)
    writeUShort(frame.total50)
    writeUShort(frame.totalGeki)
    writeUShort(frame.totalKatu)
    writeUShort(frame.totalMiss)
    writeUInt(frame.totalScore)
    writeUShort(frame.maxCombo)
    writeUShort(frame.currentCombo)
    writeBoolean(frame.perfect)
    writeUByte(frame.hp)
}

fun BinaryWriter.writeScoreFrameV294(frame: ScoreFrame) {
    writeString(frame.checksum)
    writeScoreFrameBody(frame)
}

fun BinaryWriter.writeScoreFrameV296(frame: ScoreFrame) {
    writeString(frame.checksum)
    writeInt(frame.time)
    writeScoreFrameBody(frame)
}

fun BinaryWriter.writeScoreFrameV334(frame: ScoreFrame) {
    writeInt(frame.time)
    writeScoreFrameBody(frame)
}
