package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV296
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV294
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlinx.io.Buffer
import org.junit.jupiter.api.Test

class B296Test {
    private val protocol = Protocols.select(296)

    @Test
    fun `spectate frames include time in the score frame`() {
        protocol.assertPacket(
            "10004b0000001f8b08000000000002ff636660606440028c585820c00422b81552538d932d4d521293122d0d928dccd34c8c9352cd53534d93934c8c52522dccb8d075eee086f25901e7121f096a000000",
            protocol.encode(SpectateFramesPacket(spectateBundle())),
        )
    }

    @Test
    fun `read spectate frames`() {
        val payload =
            hex(
                "1e000000d1e7be43343ba342002d00000000c497be43947a9f420b2d000000009ca7bd43b43894422b2d000000008e57bd4349b89142302d0000000074b7bc43147890423b2d0000000074b7bc43de378f42502d000000006667bc43a9f78d42652d000000006667bc43a9f78d427b2d000000005917bc4374b78c42802d000000003e77bb4374b78c428b2d00000001fce6b9433e778b42ab2d00000001e146b9433e778b42b52d00000001ab06b8433e778b42cb2d000000016976b6433e778b42d02d0000000179d5b0433e778b42f02d0000000153f4a9433e778b42052e0000000153f4a9433e778b42052e000000019893a5433e778b421b2e0000000120c3a2433e778b42202e00000001a8f29f433e778b424b2e00000001a8f29f433e778b424b2e0000000180029f4374b78c42552e0000000115829c4374b78c42702e00000001c5a19a4374b78c427b2e0000000175c1984374b78c42902e000000011891964374b78c42a52e00000001e350954374b78c42bb2e00000001a0c0934374b78c42c02e000000010d50904374b78c42cb2e00000001384f8b4374b78c42e02e0000000000000000000c0004000100030001000200b01a00000d00010000a8"
            )
        val bundle =
            readSpectateFramesV294(BinaryReader(Buffer().apply { write(payload) })) {
                readScoreFrameV296(it)
            }

        val expected =
            ReplayFrameBundle(
                action = ReplayAction.Standard,
                frames =
                    listOf(
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 381.81107f,
                            mouseY = 81.61563f,
                            time = 11520,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 381.18567f,
                            mouseY = 79.73941f,
                            time = 11531,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 379.30945f,
                            mouseY = 74.11075f,
                            time = 11563,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 378.68402f,
                            mouseY = 72.85993f,
                            time = 11568,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 377.43323f,
                            mouseY = 72.23453f,
                            time = 11579,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 377.43323f,
                            mouseY = 71.609116f,
                            time = 11600,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 376.8078f,
                            mouseY = 70.98371f,
                            time = 11621,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 376.8078f,
                            mouseY = 70.98371f,
                            time = 11643,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 376.1824f,
                            mouseY = 70.35831f,
                            time = 11648,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 374.93158f,
                            mouseY = 70.35831f,
                            time = 11659,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 371.80457f,
                            mouseY = 69.732895f,
                            time = 11691,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 370.55374f,
                            mouseY = 69.732895f,
                            time = 11701,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 368.0521f,
                            mouseY = 69.732895f,
                            time = 11723,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 364.92508f,
                            mouseY = 69.732895f,
                            time = 11728,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 353.66776f,
                            mouseY = 69.732895f,
                            time = 11760,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 339.90878f,
                            mouseY = 69.732895f,
                            time = 11781,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 339.90878f,
                            mouseY = 69.732895f,
                            time = 11781,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 331.15308f,
                            mouseY = 69.732895f,
                            time = 11803,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 325.5244f,
                            mouseY = 69.732895f,
                            time = 11808,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 319.89575f,
                            mouseY = 69.732895f,
                            time = 11851,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 319.89575f,
                            mouseY = 69.732895f,
                            time = 11851,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 318.01953f,
                            mouseY = 70.35831f,
                            time = 11861,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 313.01627f,
                            mouseY = 70.35831f,
                            time = 11888,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 309.26382f,
                            mouseY = 70.35831f,
                            time = 11899,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 305.51138f,
                            mouseY = 70.35831f,
                            time = 11920,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 301.13354f,
                            mouseY = 70.35831f,
                            time = 11941,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 298.63193f,
                            mouseY = 70.35831f,
                            time = 11963,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 295.50488f,
                            mouseY = 70.35831f,
                            time = 11968,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 288.6254f,
                            mouseY = 70.35831f,
                            time = 11979,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 278.6189f,
                            mouseY = 70.35831f,
                            time = 12000,
                        ),
                    ),
                frame = ScoreFrame(0, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, 168),
            )

        assertEquals(expected, bundle)
    }
}

private fun spectateBundle(): ReplayFrameBundle =
    ReplayFrameBundle(
        action = ReplayAction.Standard,
        frames =
            listOf(
                ReplayFrame(
                    buttonState = ButtonState.Right1,
                    mouseX = 0.0f,
                    mouseY = 0.0f,
                    time = 0,
                ),
                ReplayFrame(
                    buttonState = ButtonState.Left2,
                    mouseX = 0.0f,
                    mouseY = 0.0f,
                    time = 1,
                ),
                ReplayFrame(
                    buttonState = ButtonState.Smoke,
                    mouseX = 0.0f,
                    mouseY = 0.0f,
                    time = 2,
                ),
            ),
        frame = ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5),
    )
