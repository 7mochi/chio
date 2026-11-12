package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchChangeBeatmapPacket as InboundMatchChangeBeatmapPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.model.testStatus
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B323Test {
    private val protocol = Protocols.select(323)

    @Test
    fun `user stats with update`() {
        protocol.assertPacket(
            "0c005c0000001f8b08000000000002ff6366606060e4e62d492d2e892f2d4e2dca4bcc4d658083134e8c080e0388cdcd691c6f6060a05790972ecc2de2975a92915a9493989752ace09857929993935accc5cde29992033204002dfb4b155c000000",
            protocol.encode(UserStatsPacket(testUserInfo(status = testStatus(updateStats = true)))),
        )
    }

    @Test
    fun `user stats without update`() {
        protocol.assertPacket(
            "0c00200000001f8b08000000000002ff636600016e16cf949c540606060022197dd20f000000",
            protocol.encode(
                UserStatsPacket(testUserInfo(status = testStatus(updateStats = false)))
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
    fun `user presence emits two stats packets`() {
        protocol.assertPacket(
            "0c005c0000001f8b08000000000002ff6366606060e4e62d492d2e892f2d4e2dca4bcc4d658083134e8c080e0388cdcd691c6f6060a05790972ecc2de2975a92915a9493989752ace09857929993935accc5cde29992033204002dfb4b155c0000000c00200000001f8b08000000000002ff636600016e16cf949c540606060022197dd20f000000",
            protocol.encode(
                UserPresencePacket(testUserInfo(status = testStatus(updateStats = true)))
            ),
        )
    }

    @Test
    fun `read match change beatmap`() {
        assertEquals(
            InboundMatchChangeBeatmapPacket(inboundMatchV312()),
            protocol.decode(
                hex(
                    "3200350000001f8b08000000000002ff636664e0e60a492d2e51c84d2c49cee0e606b375154014030830b2b1300129662006005ed384082c000000"
                )
            ),
        )
    }
}

private fun inboundMatchV312(): Match =
    Match(
            id = 3,
            inProgress = true,
            name = "Test match",
            beatmapText = "Test - Test",
            beatmapId = 0,
            beatmapChecksum = "",
            slots =
                listOf(
                    MatchSlot(-1, SlotStatus.Open),
                    MatchSlot(2, SlotStatus.NotReady),
                    MatchSlot(3, SlotStatus.Ready),
                ),
        )
        .withMinimumSlotCount(8)
