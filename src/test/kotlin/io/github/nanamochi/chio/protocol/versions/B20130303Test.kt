package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchSlot
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.SlotStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130303Test {
    private val protocol = Protocols.select(20130303)

    @Test
    fun `match writes freemod slot mods`() {
        val match =
            Match(
                    id = 256,
                    inProgress = true,
                    name = "Test match",
                    password = "secret",
                    beatmapText = "Test - Test",
                    beatmapId = 0,
                    slots =
                        listOf(
                            MatchSlot(status = SlotStatus.Open, mods = Mods.Hidden),
                            MatchSlot(status = SlotStatus.Open, mods = Mods.HardRock),
                        ),
                    hostId = 2,
                    freemod = true,
                )
                .withMinimumSlotCount(8)

        protocol.assertPacket(
            "2e00006600000000010100000000000b0a54657374206d617463680b067365637265740b0b54657374202d205465737400000000000101020202020202000000000000000002000000000000010800000010000000000000000000000000000000000000000000000000000000",
            protocol.encode(MatchStartPacket(match)),
        )
    }
}
