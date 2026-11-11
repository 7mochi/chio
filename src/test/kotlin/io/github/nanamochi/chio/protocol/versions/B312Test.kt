package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchCompletePacket as InboundMatchCompletePacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.MatchStartPacket as InboundMatchStartPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.packet.types.SlotTeam
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B312Test {
    private val protocol = Protocols.select(312)

    @Test
    fun `match start ignores the match payload`() {
        protocol.assertPacket(
            "2e00140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(MatchStartPacket(Match())),
        )
    }

    @Test
    fun `match score update`() {
        protocol.assertPacket(
            "30004d0000001f8b08000000000002ffe356b04cb534334a344835b54cb134484c4ab6343749b334b34cb4303233314a4eb4e46200021e061606460666206662d820c5c0c00b6431b0020081888c613d000000",
            protocol.encode(
                MatchScoreUpdatePacket(ScoreFrame(10, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, 5))
            ),
        )
    }

    @Test
    fun `read match start`() {
        assertEquals(
            InboundMatchStartPacket,
            protocol.decode(hex("2d00140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `read match score update`() {
        assertEquals(
            InboundMatchScoreUpdatePacket(
                ScoreFrame(1234, 2, 5, 1, 0, 2, 1, 0, 9876, 12, 3, true, 100)
            ),
            protocol.decode(
                hex(
                    "2f004c0000001f8b08000000000002ffe356484a35b4484c4cb530364b4c4b354f363433344d354c4b333349b24c334b4d4cbcc4c2c0c0c4cac0c800a4c0e4143506061e066606c61400e9d7ad663d000000"
                )
            ),
        )
    }

    @Test
    fun `read match complete`() {
        assertEquals(
            InboundMatchCompletePacket,
            protocol.decode(hex("3100140000001f8b08000000000002ff03000000000000000000")),
        )
    }

    @Test
    fun `match update includes in progress`() {
        protocol.assertPacket(
            "1b00350000001f8b08000000000002ff636664e0e60a492d2e51c84d2c49cee0e606b375154014030830b2b1300129662006005ed384082c000000",
            protocol.encode(MatchUpdatePacket(matchV312())),
        )
    }

    @Test
    fun `match new includes in progress`() {
        protocol.assertPacket(
            "1c00350000001f8b08000000000002ff636664e0e60a492d2e51c84d2c49cee0e606b375154014030830b2b1300129662006005ed384082c000000",
            protocol.encode(MatchNewPacket(matchV312())),
        )
    }

    @Test
    fun `match join success includes in progress`() {
        protocol.assertPacket(
            "2500350000001f8b08000000000002ff636664e0e60a492d2e51c84d2c49cee0e606b375154014030830b2b1300129662006005ed384082c000000",
            protocol.encode(MatchJoinSuccessPacket(matchV312())),
        )
    }

    @Test
    fun `match update with id over 255 is dropped`() {
        assertEquals(0, protocol.encode(MatchUpdatePacket(matchV312().copy(id = 256))).size)
    }

    @Test
    fun `score update outside the slot range is dropped`() {
        val frame = ScoreFrame(0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0)
        assertEquals(0, protocol.encode(MatchScoreUpdatePacket(frame)).size)
    }
}

private fun matchV312(): Match =
    Match(
        id = 3,
        inProgress = true,
        name = "Test match",
        beatmapText = "Test - Test",
        beatmapId = 0,
        beatmapChecksum = "",
        slots =
            listOf(
                MatchSlot(1, SlotStatus.Open, SlotTeam.Neutral, Mods.NoMod),
                MatchSlot(2, SlotStatus.HasPlayer, SlotTeam.Neutral, Mods.NoMod),
                MatchSlot(3, SlotStatus.Ready, SlotTeam.Neutral, Mods.NoMod),
            ),
    )
