package io.github.nanamochi.chio.protocol.decode

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.ReplayAction

private fun readReplayFrameV282(reader: BinaryReader): ReplayFrame {
    val left = reader.readBoolean()
    val right = reader.readBoolean()
    val mouseX = reader.readFloat()
    val mouseY = reader.readFloat()
    val time = reader.readInt()

    var state = ButtonState.NoButton
    if (left) state += ButtonState.Left1
    if (right) state += ButtonState.Right1

    return ReplayFrame(buttonState = state, mouseX = mouseX, mouseY = mouseY, time = time)
}

private fun readReplayFrameV374(reader: BinaryReader): ReplayFrame {
    val buttonState = ButtonState(reader.readUByte())
    val legacyMouseRight = reader.readBoolean()
    val mouseX = reader.readFloat()
    val mouseY = reader.readFloat()
    val time = reader.readInt()

    val state = if (legacyMouseRight) buttonState + ButtonState.Right1 else buttonState

    return ReplayFrame(buttonState = state, mouseX = mouseX, mouseY = mouseY, time = time)
}

fun readSpectateFramesV282(reader: BinaryReader): ReplayFrameBundle {
    val frames = List(reader.readUShort()) { readReplayFrameV282(reader) }
    val action = ReplayAction.from(reader.readUByte())

    return ReplayFrameBundle(action = action, frames = frames)
}

fun readSpectateFramesV294(
    reader: BinaryReader,
    readScore: (BinaryReader) -> ScoreFrame,
): ReplayFrameBundle {
    val frames = List(reader.readUShort()) { readReplayFrameV282(reader) }
    val action = ReplayAction.from(reader.readUByte())
    val frame = if (reader.hasRemaining()) readScore(reader) else null

    return ReplayFrameBundle(action = action, frames = frames, frame = frame)
}

fun readSpectateFramesV374(
    reader: BinaryReader,
    readScore: (BinaryReader) -> ScoreFrame,
): ReplayFrameBundle {
    val frames = List(reader.readUShort()) { readReplayFrameV374(reader) }
    val action = ReplayAction.from(reader.readUByte())
    val frame = if (reader.hasRemaining()) readScore(reader) else null

    return ReplayFrameBundle(action = action, frames = frames, frame = frame)
}
