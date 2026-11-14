package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.TitleUpdate
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class B1820Test {
    private val protocol = Protocols.select(1820)

    @Test
    fun `title update sends the image url`() {
        protocol.assertPacket(
            "4c00001f0000000b1d68747470733a2f2f6578616d706c652e636f6d2f696d6167652e706e67",
            protocol.encode(TitleUpdatePacket(TitleUpdate("https://example.com/image.png"))),
        )
    }

    @Test
    fun `title update without an image url is dropped`() {
        assertEquals(0, protocol.encode(TitleUpdatePacket(TitleUpdate())).size)
    }

    @Test
    fun `b1817 title update stays empty`() {
        Protocols.select(1817)
            .assertPacket(
                "4c000000000000",
                Protocols.select(1817)
                    .encode(TitleUpdatePacket(TitleUpdate("https://example.com/image.png"))),
            )
    }
}
