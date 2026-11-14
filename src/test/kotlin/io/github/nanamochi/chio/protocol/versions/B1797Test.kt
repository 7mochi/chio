package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B1797Test {
    private val protocol = Protocols.select(1797)
    private val info =
        testUserInfo(stats = testStats(rank = 100_000)).let {
            it.copy(
                presence =
                    it.presence.copy(
                        countryIndex = 10,
                        city = "Test City",
                        longitude = 1.5,
                        latitude = -2.5,
                    )
            )
        }

    @Test
    fun `user presence includes rank`() {
        protocol.assertPacket(
            "5300002e000000030000000b0d746573745f757365726e616d6501130a0b09546573742043697479010000c03f000020c0a0860100",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `protocol negotiation defaults to version seven`() {
        protocol.assertPacket(
            "4b00000400000007000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `b1796 presence omits rank`() {
        val older = Protocols.select(1796)

        older.assertPacket(
            "5300002a000000030000000b0d746573745f757365726e616d6501130a0b09546573742043697479010000c03f000020c0",
            older.encode(UserPresencePacket(info)),
        )
    }
}
