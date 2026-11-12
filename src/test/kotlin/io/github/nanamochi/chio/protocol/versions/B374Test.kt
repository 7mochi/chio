package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B374Test {
    private val protocol = Protocols.select(374)

    @Test
    fun `spectate frames use button state`() {
        protocol.assertPacket(
            "1000003a00000002000200000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c0003000164",
            protocol.encode(SpectateFramesPacket(bundleV374())),
        )
    }

    @Test
    fun `read spectate frames with button state`() {
        assertEquals(
            InboundSpectateFramesPacket(bundleV374()),
            protocol.decode(
                hex(
                    "1300003a00000002000200000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c0003000164"
                )
            ),
        )
    }

    @Test
    fun `b365 keeps legacy mouse buttons`() {
        val older = Protocols.select(365)

        older.assertPacket(
            "1000003a00000002000001000000000000000000000000010000000000000000000100000000d204000002050001000000020001000000942600000c0003000164",
            older.encode(SpectateFramesPacket(bundleV374())),
        )
    }
}

private fun bundleV374(): ReplayFrameBundle =
    ReplayFrameBundle(
        action = ReplayAction.Standard,
        frames =
            listOf(
                ReplayFrame(buttonState = ButtonState.Right1, mouseX = 0f, mouseY = 0f, time = 0),
                ReplayFrame(buttonState = ButtonState.Left1, mouseX = 0f, mouseY = 0f, time = 1),
            ),
        frame = scoreV374(),
    )

private fun scoreV374(): ScoreFrame = ScoreFrame(1234, 2, 5, 1, 0, 2, 1, 0, 9876, 12, 3, true, 100)
