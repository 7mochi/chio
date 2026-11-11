package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.inbound.PrivateMessagePacket
import io.github.nanamochi.chio.packet.outbound.MessagePacket as OutboundMessagePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMessageV320
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV320

/** b320 adds the target field to messages and partial multi-channel support. */
val B320: Protocol =
    B312.derive(320) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMessage) { reader -> InboundMessagePacket(readMessageV320(reader)) }
    decode(PacketType.OsuPrivateMessage) { reader ->
        PrivateMessagePacket(readMessageV320(reader))
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<OutboundMessagePacket> { protocol, packet ->
            val writer = BinaryWriter()
            writer.writeMessageV320(packet.message, protocol.formatChatLinks)
            listOf(EncodedPacket(PacketType.BanchoMessage, writer.toByteArray()))
        }
    )
}
