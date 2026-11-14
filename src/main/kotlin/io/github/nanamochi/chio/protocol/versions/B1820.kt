package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b1820 adds the image url to the title update packet. */
val B1820: Protocol =
    B1817.derive(1820) {
        encode(
            PacketEncoder<TitleUpdatePacket> { _, packet ->
                val imageUrl = packet.update.imageUrl
                if (imageUrl.isEmpty()) {
                    emptyList()
                } else {
                    val writer = BinaryWriter()
                    writer.writeString(imageUrl)
                    listOf(EncodedPacket(PacketType.BanchoTitleUpdate, writer.toByteArray()))
                }
            }
        )
    }
