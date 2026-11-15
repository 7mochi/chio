package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.model.TitleUpdate
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20121221Test {
    private val protocol = Protocols.select(20121221)

    @Test
    fun `title update sends the image and redirect urls`() {
        val update =
            TitleUpdate(
                imageUrl = "https://example.com/image.png",
                redirectUrl = "https://example.com/redirect",
            )

        protocol.assertPacket(
            "4c00003c0000000b3a68747470733a2f2f6578616d706c652e636f6d2f696d6167652e706e677c68747470733a2f2f6578616d706c652e636f6d2f7265646972656374",
            protocol.encode(TitleUpdatePacket(update)),
        )
    }
}
