package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20151106Test {
    private val protocol = Protocols.select(20151106)
    private val frame = scoreFrame()

    @Test
    fun `score frame writes scorev2 fields`() {
        protocol.assertPacket(
            "3000002d0000007b000000010200030004000500060007000800000009000a00010b0c01000000000000e03f000000000000d03f",
            protocol.encode(MatchScoreUpdatePacket(frame)),
        )
    }

    @Test
    fun `read score frame reads scorev2 fields`() {
        assertEquals(
            InboundMatchScoreUpdatePacket(frame),
            protocol.decode(
                hex(
                    "2f00002d0000007b000000010200030004000500060007000800000009000a00010b0c01000000000000e03f000000000000d03f"
                )
            ),
        )
    }
}

private fun scoreFrame(): ScoreFrame =
    ScoreFrame(
        time = 123,
        id = 1,
        total300 = 2,
        total100 = 3,
        total50 = 4,
        totalGeki = 5,
        totalKatu = 6,
        totalMiss = 7,
        totalScore = 8,
        maxCombo = 9,
        currentCombo = 10,
        perfect = true,
        hp = 11,
        tagByte = 12,
        usingScoreV2 = true,
        comboPortion = 0.5,
        bonusPortion = 0.25,
    )
