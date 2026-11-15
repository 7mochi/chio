package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20121221 adds both the image url and redirect url to the title update packet. */
val B20121221: Protocol =
    B20121212.derive(20121221) {
        encode<TitleUpdatePacket>(PacketType.BanchoTitleUpdate) {
            writeString("${it.update.imageUrl}|${it.update.redirectUrl}")
        }
    }
