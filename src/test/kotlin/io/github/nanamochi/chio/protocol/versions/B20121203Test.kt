package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20121203Test {
    private val protocol = Protocols.select(20121203)
    private val info =
        testUserInfo(stats = testStats(rank = 100_000)).let {
            it.copy(
                presence =
                    it.presence.copy(
                        countryIndex = 10,
                        city = "Test City",
                        longitude = 1.5,
                        latitude = -2.5,
                    ),
                status = it.status.copy(mode = Mode.OsuMania),
            )
        }

    @Test
    fun `user presence packs the mode into permissions`() {
        protocol.assertPacket(
            "53000023000000030000000b0d746573745f757365726e616d65130a610000c03f000020c0a086010003",
            protocol.encode(UserPresencePacket(info)),
        )
    }
}
