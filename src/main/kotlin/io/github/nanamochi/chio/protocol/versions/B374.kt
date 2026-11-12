package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV334
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV374
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV334
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV374

/** b374 replaces the replay frame mouse buttons with a button state byte. */
val B374: Protocol =
    B365.derive(374) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV374(reader) { readScoreFrameV334(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV374(packet.bundle) { writeScoreFrameV334(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
}
