package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.inbound.CantSpectatePacket
import io.github.nanamochi.chio.packet.inbound.ErrorReportPacket
import io.github.nanamochi.chio.packet.inbound.ExitPacket
import io.github.nanamochi.chio.packet.inbound.PongPacket
import io.github.nanamochi.chio.packet.inbound.StartSpectatingPacket
import io.github.nanamochi.chio.packet.inbound.StatusUpdateRequestPacket
import io.github.nanamochi.chio.packet.inbound.StopSpectatingPacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.model.ReplayFrame
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.model.UserQuit
import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.InvitePacket
import io.github.nanamochi.chio.packet.outbound.IrcChangeUsernamePacket
import io.github.nanamochi.chio.packet.outbound.IrcQuitPacket
import io.github.nanamochi.chio.packet.outbound.LoginReplyPacket
import io.github.nanamochi.chio.packet.outbound.MessagePacket
import io.github.nanamochi.chio.packet.outbound.PingPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.SpectatorCantSpectatePacket
import io.github.nanamochi.chio.packet.outbound.SpectatorJoinedPacket
import io.github.nanamochi.chio.packet.outbound.SpectatorLeftPacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceBundlePacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceSinglePacket
import io.github.nanamochi.chio.packet.outbound.UserQuitPacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.outbound.VersionUpdatePacket
import io.github.nanamochi.chio.packet.types.ButtonState
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.QuitState
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.packet.types.Status
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.decode.readChannelMessageV282
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV282
import io.github.nanamochi.chio.protocol.decode.readUserStatusV282
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlinx.io.Buffer
import org.junit.jupiter.api.Test

class B282Test {
    private val protocol = Protocols.select(282)

    @Test
    fun `login reply`() {
        protocol.assertPacket(
            "0500180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(LoginReplyPacket(3)),
        )
    }

    @Test
    fun ping() {
        protocol.assertPacket(
            "0800140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(PingPacket),
        )
    }

    @Test
    fun `message to osu channel`() {
        protocol.assertPacket(
            "07002d0000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d35301d614a6031d000000",
            protocol.encode(MessagePacket(Message("test_username", "test_message", "#osu", 3))),
        )
    }

    @Test
    fun `message to unsupported channel is dropped`() {
        assertEquals(
            0,
            protocol
                .encode(MessagePacket(Message("test_username", "test_message", "#test", 3)))
                .size,
        )
    }

    @Test
    fun `irc change username`() {
        protocol.assertPacket(
            "0900290000001f8b08000000000002ffe396c9cf49892f2d4e2dca4bcc4db50382bcd472381f004ebed0c61e000000",
            protocol.encode(IrcChangeUsernamePacket("old_username", "new_username")),
        )
    }

    @Test
    fun `user stats normal user idle`() {
        protocol.assertPacket(
            "0c005a0000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d819bc5332507a443985bc42fb52423b52827312fa558c131af24332727b518005a5083b25f000000",
            protocol.encode(UserStatsPacket(testUserInfo())),
        )
    }

    @Test
    fun `user stats normal user lobby`() {
        protocol.assertPacket(
            "0c00550000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d0b2426cc2de2975a92915a9493989752ace09857929993935a0c002382ba8e5a000000",
            protocol.encode(
                UserStatsPacket(testUserInfo(status = testStatus(action = Status.Lobby, text = "")))
            ),
        )
    }

    @Test
    fun `user stats irc user`() {
        protocol.assertPacket(
            "0b00230000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4d0500e19e94dc0f000000",
            protocol.encode(UserStatsPacket(testUserInfo(isIrc = true))),
        )
    }

    @Test
    fun `user quit normal user`() {
        protocol.assertPacket(
            "0d00550000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d0b2426cc2de2975a92915a9493989752ace09857929993935a0c002382ba8e5a000000",
            protocol.encode(
                UserQuitPacket(
                    UserQuit(
                        testUserInfo(status = testStatus(action = Status.Lobby, text = "")),
                        QuitState.Gone,
                    )
                )
            ),
        )
    }

    @Test
    fun `user quit irc user`() {
        protocol.assertPacket(
            "0d00230000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4d0500e19e94dc0f000000",
            protocol.encode(
                UserQuitPacket(UserQuit(testUserInfo(isIrc = true), QuitState.IrcRemaining))
            ),
        )
    }

    @Test
    fun `spectator joined`() {
        protocol.assertPacket(
            "0e00180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(SpectatorJoinedPacket(3)),
        )
    }

    @Test
    fun `spectator left`() {
        protocol.assertPacket(
            "0f00180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(SpectatorLeftPacket(3)),
        )
    }

    @Test
    fun `spectate frames`() {
        protocol.assertPacket(
            "1000210000001f8b08000000000002ff636660606440028c585820c0042200c36757962d000000",
            protocol.encode(SpectateFramesPacket(spectateBundle())),
        )
    }

    @Test
    fun `version update`() {
        protocol.assertPacket(
            "1400140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(VersionUpdatePacket),
        )
    }

    @Test
    fun `spectator cant spectate`() {
        protocol.assertPacket(
            "1700180000001f8b08000000000002ff636660600000f270f13304000000",
            protocol.encode(SpectatorCantSpectatePacket(3)),
        )
    }

    @Test
    fun `user presence delegates to stats`() {
        protocol.assertPacket(
            "0c005a0000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d819bc5332507a443985bc42fb52423b52827312fa558c131af24332727b518005a5083b25f000000",
            protocol.encode(UserPresencePacket(testUserInfo())),
        )
    }

    @Test
    fun `user presence normal user lobby`() {
        protocol.assertPacket(
            "0c00550000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d0b2426cc2de2975a92915a9493989752ace09857929993935a0c002382ba8e5a000000",
            protocol.encode(
                UserPresencePacket(
                    testUserInfo(status = testStatus(action = Status.Lobby, text = ""))
                )
            ),
        )
    }

    @Test
    fun `user presence irc user idle`() {
        protocol.assertPacket(
            "0b00230000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4d0500e19e94dc0f000000",
            protocol.encode(UserPresencePacket(testUserInfo(isIrc = true))),
        )
    }

    @Test
    fun `user presence single normal user idle`() {
        protocol.assertPacket(
            "0c005a0000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d819bc5332507a443985bc42fb52423b52827312fa558c131af24332727b518005a5083b25f000000",
            protocol.encode(UserPresenceSinglePacket(testUserInfo())),
        )
    }

    @Test
    fun `user presence single normal user lobby`() {
        protocol.assertPacket(
            "0c00550000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d0b2426cc2de2975a92915a9493989752ace09857929993935a0c002382ba8e5a000000",
            protocol.encode(
                UserPresenceSinglePacket(
                    testUserInfo(status = testStatus(action = Status.Lobby, text = ""))
                )
            ),
        )
    }

    @Test
    fun `user presence single irc user idle`() {
        protocol.assertPacket(
            "0b00230000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4d0500e19e94dc0f000000",
            protocol.encode(UserPresenceSinglePacket(testUserInfo(isIrc = true))),
        )
    }

    @Test
    fun `user presence bundle emits one packet per user`() {
        protocol.assertPacket(
            "0c00550000001f8b08000000000002ff63666060e0e62d492d2e892f2d4e2dca4bcc4d654001910e8c483c109b9bd338dec0c040af202f9d0b2426cc2de2975a92915a9493989752ace09857929993935a0c002382ba8e5a0000000c00580000001f8b08000000000002ff63616060e0e62f492d2e892f2d4e2dca4bcc4d8d3762400607221c9891b84c200d9c26f10606067a0579e95c2031616e11bfd4928cd4a29cc4bc946205c7bc92cc9c9cd46200aaaa0f145c000000",
            protocol.encode(
                UserPresenceBundlePacket(
                    listOf(
                        testUserInfo(status = testStatus(action = Status.Lobby, text = "")),
                        testUserInfo(
                            id = 4,
                            name = "test_username_2",
                            status = testStatus(action = Status.Lobby, text = ""),
                            stats = testStats(rank = 2, accuracy = 99.0f, playcount = 3),
                        ),
                    )
                )
            ),
        )
    }

    @Test
    fun `read user status`() {
        val payload =
            hex(
                "020b2e4d7574737568696b6f20497a756d69202d2052656420476f6f7365205b5261696e626f7720426567696e6e65725d0b2031386162306435653439353362616162343062373661646562363334326163300000"
            )
        val status = readUserStatusV282(BinaryReader(Buffer().apply { write(payload) }))
        assertEquals(Status.Playing, status.action)
        assertEquals("Mutsuhiko Izumi - Red Goose [Rainbow Beginner]", status.text)
        assertEquals("18ab0d5e4953baab40b76adeb6342ac0", status.beatmapChecksum)
        assertEquals(Mods.NoMod, status.mods)
    }

    @Test
    fun `read message`() {
        val message =
            readChannelMessageV282(BinaryReader(Buffer().apply { write(hex("0b052168656c70")) }))
        assertEquals(Message(sender = "", content = "!help", target = "#osu"), message)
    }

    @Test
    fun `read spectate frames`() {
        val payload =
            hex(
                "1e000000d2ec2c43882f3d43000200000000d2ec2c43882f3d43100200000000d2ec2c43882f3d43250200000000d2ec2c43882f3d43300200000000d2ec2c43882f3d43450200000000d2ec2c43882f3d43550200000000d2ec2c43882f3d43600200000000d2ec2c43882f3d43750200000000d2ec2c43882f3d438b0200000000d2ec2c43882f3d43950200000000d2ec2c43882f3d43a50200000000d2ec2c43882f3d43bb0200000000d2ec2c43882f3d43c50200000000d2ec2c43882f3d43db0200000000d2ec2c43882f3d43f00200000000d2ec2c43882f3d43f50200000000d2ec2c43882f3d430b0300000000d2ec2c43882f3d43200300000000d2ec2c43882f3d432b03000000009cac2b436d8f3c43400300000000676c2a4352ef3b435003000000004ccc294352ef3b435b03000000004ccc294352ef3b437003000000004ccc294352ef3b438503000000004ccc294352ef3b439003000000004ccc294352ef3b43a003000000004ccc294352ef3b43b50300000000322c294352ef3b43c00300000000178c2843384f3b43d50300000000fceb2743384f3b43e503000000"
            )
        val bundle = readSpectateFramesV282(BinaryReader(Buffer().apply { write(payload) }))

        val expected =
            ReplayFrameBundle(
                action = ReplayAction.Standard,
                frames =
                    listOf(
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 512,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 528,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 549,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 560,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 581,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 597,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 608,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 629,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 651,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 661,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 677,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 699,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 709,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 731,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 752,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 757,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 779,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 800,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 172.92508f,
                            mouseY = 189.18567f,
                            time = 811,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 171.67426f,
                            mouseY = 188.56026f,
                            time = 832,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 170.42345f,
                            mouseY = 187.93484f,
                            time = 848,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 859,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 880,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 901,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 912,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 928,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.79803f,
                            mouseY = 187.93484f,
                            time = 949,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 169.17264f,
                            mouseY = 187.93484f,
                            time = 960,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 168.54723f,
                            mouseY = 187.30945f,
                            time = 981,
                        ),
                        ReplayFrame(
                            buttonState = ButtonState(0),
                            mouseX = 167.92181f,
                            mouseY = 187.30945f,
                            time = 997,
                        ),
                    ),
                frame = null,
            )

        assertEquals(expected, bundle)
    }

    @Test
    fun `read exit`() {
        assertEquals(
            ExitPacket,
            protocol.decode(hex("0200140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `read start spectating`() {
        assertEquals(
            StartSpectatingPacket(42),
            protocol.decode(hex("1100180000001f8b08000000000002ffd362606000004690cbee04000000")),
        )
    }

    @Test
    fun `read error report`() {
        assertEquals(
            ErrorReportPacket("test error"),
            protocol.decode(
                hex("1500200000001f8b08000000000002ffe3e62a492d2e51482d2aca2f0200fdf715a70c000000")
            ),
        )
    }

    @Test
    fun `read pong`() {
        assertEquals(
            PongPacket,
            protocol.decode(hex("0400140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `read status update request`() {
        assertEquals(
            StatusUpdateRequestPacket,
            protocol.decode(hex("0300140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `read stop spectating`() {
        assertEquals(
            StopSpectatingPacket,
            protocol.decode(hex("1200140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `read cant spectate`() {
        assertEquals(
            CantSpectatePacket,
            protocol.decode(hex("1600140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `irc quit`() {
        protocol.assertPacket(
            "0a001f0000001f8b08000000000002ffe3e62c492d2e89cf4bcc4d0500055de0090b000000",
            protocol.encode(IrcQuitPacket("test_name")),
        )
    }

    @Test
    fun `invite is sent as a message`() {
        protocol.assertPacket(
            "07002d0000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4de5e6017373538b8b13d35301d614a6031d000000",
            protocol.encode(InvitePacket(Message("test_username", "test_message", "#osu", 3))),
        )
    }

    @Test
    fun `user quit while osu remains is dropped`() {
        val quit = UserQuit(testUserInfo(), QuitState.OsuRemaining)
        assertEquals(0, protocol.encode(UserQuitPacket(quit)).size)
    }

    @Test
    fun `user quit irc user who leaves sends irc quit`() {
        protocol.assertPacket(
            "0a00230000001f8b08000000000002ffe3e62d492d2e892f2d4e2dca4bcc4d0500e19e94dc0f000000",
            protocol.encode(UserQuitPacket(UserQuit(testUserInfo(isIrc = true), QuitState.Gone))),
        )
    }
}

private fun spectateBundle(): ReplayFrameBundle {
    val score = ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5)
    return ReplayFrameBundle(
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
        frame = score,
    )
}
