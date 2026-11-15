package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.packet.types.ReplayAction
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import io.github.nanamochi.chio.protocol.hex
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B20160404Test {
    private val protocol = Protocols.select(20160404)
    private val bundle = ReplayFrameBundle(action = ReplayAction.Standard, extra = 42, sequence = 7)

    @Test
    fun `spectate frames write the sequence`() {
        protocol.assertPacket(
            "0f0000090000002a0000000000000700",
            protocol.encode(SpectateFramesPacket(bundle)),
        )
    }

    @Test
    fun `read spectate frames reads the sequence`() {
        assertEquals(
            InboundSpectateFramesPacket(bundle),
            protocol.decode(hex("120000090000002a0000000000000700")),
        )
    }
}
