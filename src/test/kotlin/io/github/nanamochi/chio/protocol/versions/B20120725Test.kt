package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.Channel
import io.github.nanamochi.chio.packet.outbound.ChannelAvailableAutojoinPacket
import io.github.nanamochi.chio.packet.outbound.ChannelAvailablePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20120725Test {
    private val protocol = Protocols.select(20120725)
    private val channel = Channel(name = "#osu", topic = "Test topic", userCount = 123)

    @Test
    fun `channel available includes topic and user count`() {
        protocol.assertPacket(
            "410000140000000b04236f73750b0a5465737420746f7069637b00",
            protocol.encode(ChannelAvailablePacket(channel)),
        )
    }

    @Test
    fun `autojoin channel includes topic and user count`() {
        protocol.assertPacket(
            "430000140000000b04236f73750b0a5465737420746f7069637b00",
            protocol.encode(ChannelAvailableAutojoinPacket(channel)),
        )
    }
}
