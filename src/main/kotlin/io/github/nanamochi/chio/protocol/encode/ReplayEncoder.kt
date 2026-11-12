package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.types.ButtonState

fun BinaryWriter.writeReplayFrameV282(frame: ReplayFrame) {
    val left = ButtonState.Left1 in frame.buttonState || ButtonState.Left2 in frame.buttonState
    val right = ButtonState.Right1 in frame.buttonState || ButtonState.Right2 in frame.buttonState

    writeBoolean(left)
    writeBoolean(right)
    writeFloat(frame.mouseX)
    writeFloat(frame.mouseY)
    writeInt(frame.time)
}

fun BinaryWriter.writeReplayFrameV374(frame: ReplayFrame) {
    writeUByte(frame.buttonState.bits)
    writeBoolean(false)
    writeFloat(frame.mouseX)
    writeFloat(frame.mouseY)
    writeInt(frame.time)
}

fun BinaryWriter.writeSpectateFramesV282(bundle: ReplayFrameBundle) {
    writeUShort(bundle.frames.size)
    bundle.frames.forEach { writeReplayFrameV282(it) }
    writeUByte(bundle.action.value)
}

fun BinaryWriter.writeSpectateFramesV294(
    bundle: ReplayFrameBundle,
    writeScore: BinaryWriter.(ScoreFrame) -> Unit,
) {
    writeUShort(bundle.frames.size)
    bundle.frames.forEach { writeReplayFrameV282(it) }
    writeUByte(bundle.action.value)
    bundle.frame?.let { this.writeScore(it) }
}

fun BinaryWriter.writeSpectateFramesV374(
    bundle: ReplayFrameBundle,
    writeScore: BinaryWriter.(ScoreFrame) -> Unit,
) {
    writeUShort(bundle.frames.size)
    bundle.frames.forEach { writeReplayFrameV374(it) }
    writeUByte(bundle.action.value)
    bundle.frame?.let { this.writeScore(it) }
}
