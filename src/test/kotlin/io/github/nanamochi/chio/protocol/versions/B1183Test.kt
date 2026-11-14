package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.testStats
import io.github.nanamochi.chio.packet.model.testUserInfo
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B1183Test {
    private val protocol = Protocols.select(1183)
    private val info =
        testUserInfo(stats = testStats(rank = 100_000)).let {
            it.copy(presence = it.presence.copy(longitude = 1.5, latitude = -2.5))
        }

    @Test
    fun `user presence includes location`() {
        protocol.assertPacket(
            "0c00006b000000030000000200010b0449646c65000000000000000000000000000000000000c842010000000000000000000000a08601000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c6573010000c03f000020c0",
            protocol.encode(UserPresencePacket(info)),
        )
    }

    @Test
    fun `protocol negotiation defaults to version five`() {
        protocol.assertPacket(
            "4c00000400000005000000",
            protocol.encode(ProtocolNegotiationPacket()),
        )
    }

    @Test
    fun `b695 presence omits location`() {
        val older = Protocols.select(695)

        older.assertPacket(
            "0c000063000000030000000200010b0449646c65000000000000000000000000000000000000c842010000000000000000000000a08601000b0d746573745f757365726e616d650b09335f3030302e706e67130b144e65746865726c616e647320416e74696c6c657301",
            older.encode(UserPresencePacket(info)),
        )
    }
}
