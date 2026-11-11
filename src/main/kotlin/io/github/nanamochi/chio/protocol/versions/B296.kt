package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV296
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV294
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV296
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV294

/** b296 adds the time field to score frames. */
val B296: Protocol =
    B294.derive(296) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV294(reader) { readScoreFrameV296(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV294(packet.bundle) { writeScoreFrameV296(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
}
