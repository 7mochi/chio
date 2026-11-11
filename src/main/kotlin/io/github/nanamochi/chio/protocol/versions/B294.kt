package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.inbound.PrivateMessagePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.MessagePacket as OutboundMessagePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readChannelMessageV282
import io.github.nanamochi.chio.protocol.decode.readPrivateMessageV294
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV294
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV294
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV294
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV294
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV294

/** b294 adds private messages and a score frame inside spectate frames. */
val B294: Protocol =
    B291.derive(294) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMessage) { reader ->
        InboundMessagePacket(readChannelMessageV282(reader))
    }
    decode(PacketType.OsuPrivateMessage) { reader ->
        PrivateMessagePacket(readPrivateMessageV294(reader))
    }
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV294(reader) { readScoreFrameV294(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<OutboundMessagePacket> { protocol, packet ->
            val message = packet.message
            if (!message.isDirectMessage && message.target !in protocol.autojoinChannels) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMessageV294(message, protocol.formatChatLinks)
                listOf(EncodedPacket(PacketType.BanchoMessage, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV294(packet.bundle) { writeScoreFrameV294(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
}
