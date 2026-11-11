package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.AnnouncePacket
import io.github.nanamochi.chio.packet.outbound.GetAttentionPacket
import io.github.nanamochi.chio.packet.outbound.RestartPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B291Test {
    private val protocol = Protocols.select(291)

    @Test
    fun `get attention`() {
        protocol.assertPacket(
            "1800140000001f8b08000000000002ff03000000000000000000",
            protocol.encode(GetAttentionPacket),
        )
    }

    @Test
    fun announce() {
        protocol.assertPacket(
            "19001a0000001f8b08000000000002ffe36629492d2e0100b056bed706000000",
            protocol.encode(AnnouncePacket("test")),
        )
    }

    @Test
    fun `restart is backported as an announce`() {
        protocol.assertPacket(
            "19003a0000001f8b08000000000002ffe356714acc4bcec857c82c56284a2d2e492c2ac9cc4bd75128c8494d2c4e55284fcc2cd1d3d303007419719e26000000",
            protocol.encode(RestartPacket(1000)),
        )
    }
}
