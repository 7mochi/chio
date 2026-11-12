package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.packet.inbound.BeatmapInfoRequestPacket
import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.outbound.BeatmapInfoReplyPacket
import io.github.nanamochi.chio.packet.types.Rank
import io.github.nanamochi.chio.packet.types.RankedStatus
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class B354Test {
    private val protocol = Protocols.select(354)

    @Test
    fun `beatmap info reply`() {
        protocol.assertPacket(
            "4600002f00000002000000000087d61200b1cb74002a00000001040b08636865636b73756d0100ffffffff00000000ffffffff000900",
            protocol.encode(
                BeatmapInfoReplyPacket(
                    listOf(
                        BeatmapInfo(
                            index = 0,
                            beatmapId = 1234567,
                            beatmapSetId = 7654321,
                            threadId = 42,
                            rankedStatus = RankedStatus.Ranked,
                            osuRank = Rank.A,
                            checksum = "checksum",
                        ),
                        BeatmapInfo(
                            index = 1,
                            beatmapId = -1,
                            beatmapSetId = 0,
                            threadId = -1,
                            rankedStatus = RankedStatus.Pending,
                            osuRank = Rank.N,
                            checksum = "",
                        ),
                    )
                )
            ),
        )
    }

    @Test
    fun `read beatmap info request`() {
        assertEquals(
            BeatmapInfoRequestPacket(listOf("a.osu", "b.osu")),
            protocol.decode(hex("45000012000000020000000b05612e6f73750b05622e6f7375")),
        )
    }

    @Test
    fun `b349 doesn't support beatmap info`() {
        val older = Protocols.select(349)

        assertFailsWith<UnsupportedPacketException> {
            older.encode(BeatmapInfoReplyPacket(emptyList()))
        }
        assertFailsWith<UnsupportedPacketException> {
            older.decode(hex("45000012000000020000000b05612e6f73750b05622e6f7375"))
        }
    }
}
