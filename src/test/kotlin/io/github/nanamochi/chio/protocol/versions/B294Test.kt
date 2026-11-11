package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MessagePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.decode.readChannelMessageV282
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV294
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV294
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlinx.io.Buffer
import org.junit.jupiter.api.Test

class B294Test {
    private val protocol = Protocols.select(294)

    @Test
    fun `message to osu channel`() {
        protocol.assertPacket(
            "07002e0000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d3531900783eb1bd1e000000",
            protocol.encode(MessagePacket(Message("test_username", "test_message", "#osu", 3))),
        )
    }

    @Test
    fun `private message`() {
        protocol.assertPacket(
            "07002e0000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d3531901ee0eb6ca1e000000",
            protocol.encode(
                MessagePacket(Message("test_username", "test_message", "test_username_2", 3))
            ),
        )
    }

    @Test
    fun `unsupported channel is dropped`() {
        assertEquals(
            0,
            protocol
                .encode(MessagePacket(Message("test_username", "test_message", "#test", 3)))
                .size,
        )
    }

    @Test
    fun `spectate frames include a score frame`() {
        protocol.assertPacket(
            "10004b0000001f8b08000000000002ff636660606440028c585820c00422b81552538d932d4d521293122d0d928dccd34c8c9352cd53534d93934c8c52522dcc5074ede0869ac20a006ab2dc0466000000",
            protocol.encode(SpectateFramesPacket(spectateBundle())),
        )
    }

    @Test
    fun `read message`() {
        val message =
            readChannelMessageV282(BinaryReader(Buffer().apply { write(hex("0b0473686974")) }))
        assertEquals(Message("", "shit", "#osu"), message)
    }

    @Test
    fun `read spectate frames`() {
        val payload =
            hex(
                "1e00000078d09243947a9f420b0000000000d80f8f43de378f421500000000008a3e87433c687142350000000000a4d9794310645842400000000000e3556343cfdd3242500000000000b8514a4324d70a425b000000000078d042431ba5de417b000000000028f0404370a3d441850000000000d80f3f43c5a1ca41900000000000a2cf3d43c5a1ca41a000000000006d8f3c43f0a0c541b50000000000e86e394370a3d441cb0000000000624e36439ca7ed41d50000000000f7cd334346a9f741e0000000000022cd2e43f9d70f42fb0000000000322c294324dc28420501000000000c4b224325e146421b01000000009789194326e66442250100000000010afc4233368542400100000000f6c3d7429eb687424b01000000004081c7429eb687425501000000000b41c6429eb687426b01000000000b41c6429eb687428001000000014081c7429eb687429001000000014081c7429eb687429b01000000014081c74269768642a501000000014081c74269768642c501000000004081c74269768642d001000000004081c74269768642e001000000004081c74269768642eb01000000"
            )
        val bundle =
            readSpectateFramesV294(BinaryReader(Buffer().apply { write(payload) })) {
                readScoreFrameV294(it)
            }

        val expected =
            ReplayFrameBundle(
                action = ReplayAction.Standard,
                frames =
                    listOf(
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 293.62866f,
                            mouseY = 79.73941f,
                            time = 11,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 286.12378f,
                            mouseY = 71.609116f,
                            time = 21,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 270.4886f,
                            mouseY = 60.35179f,
                            time = 53,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 249.85016f,
                            mouseY = 54.097717f,
                            time = 64,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 227.3355f,
                            mouseY = 44.71661f,
                            time = 80,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 202.31921f,
                            mouseY = 34.7101f,
                            time = 91,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 194.81433f,
                            mouseY = 27.830618f,
                            time = 123,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 192.93811f,
                            mouseY = 26.579803f,
                            time = 133,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 191.06189f,
                            mouseY = 25.328989f,
                            time = 144,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 189.81107f,
                            mouseY = 25.328989f,
                            time = 160,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 188.56026f,
                            mouseY = 24.703583f,
                            time = 181,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 185.43323f,
                            mouseY = 26.579803f,
                            time = 203,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 182.30618f,
                            mouseY = 29.70684f,
                            time = 213,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 179.80455f,
                            mouseY = 30.957653f,
                            time = 224,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 174.8013f,
                            mouseY = 35.96091f,
                            time = 251,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.17264f,
                            mouseY = 42.21498f,
                            time = 261,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 162.29315f,
                            mouseY = 49.719868f,
                            time = 283,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 153.53746f,
                            mouseY = 57.224754f,
                            time = 293,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 126.01954f,
                            mouseY = 66.60586f,
                            time = 320,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 107.88274f,
                            mouseY = 67.856674f,
                            time = 331,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.75244f,
                            mouseY = 67.856674f,
                            time = 341,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.12704f,
                            mouseY = 67.856674f,
                            time = 363,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.12704f,
                            mouseY = 67.856674f,
                            time = 384,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 99.75244f,
                            mouseY = 67.856674f,
                            time = 400,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 99.75244f,
                            mouseY = 67.856674f,
                            time = 411,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 99.75244f,
                            mouseY = 67.23127f,
                            time = 421,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(2),
                            mouseX = 99.75244f,
                            mouseY = 67.23127f,
                            time = 453,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.75244f,
                            mouseY = 67.23127f,
                            time = 464,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.75244f,
                            mouseY = 67.23127f,
                            time = 480,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 99.75244f,
                            mouseY = 67.23127f,
                            time = 491,
                        ),
                    ),
                frame = null,
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
