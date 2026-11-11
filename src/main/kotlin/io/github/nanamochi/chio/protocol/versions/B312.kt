package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchCompletePacket as InboundMatchCompletePacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.MatchStartPacket as InboundMatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket as OutboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket as OutboundMatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV296
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV312
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV296

/** b312 adds match start/update and `inProgress` inside the match struct. */
val B312: Protocol =
    B298.derive(312) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchStart) { _ -> InboundMatchStartPacket }
    decode(PacketType.OsuMatchScoreUpdate) { reader ->
        InboundMatchScoreUpdatePacket(readScoreFrameV296(reader))
    }
    decode(PacketType.OsuMatchComplete) { _ -> InboundMatchCompletePacket }
}

private fun ProtocolBuilder.registerOutbound() {
    encodeEmpty<OutboundMatchStartPacket>(PacketType.BanchoMatchStart)
    encode(
        PacketEncoder<OutboundMatchScoreUpdatePacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeScoreFrameV296(packet.frame)
            listOf(EncodedPacket(PacketType.BanchoMatchScoreUpdate, writer.toByteArray()))
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMatchV312(packet.match, protocol.slotSize)
                listOf(EncodedPacket(PacketType.BanchoMatchUpdate, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMatchV312(packet.match, protocol.slotSize)
                listOf(EncodedPacket(PacketType.BanchoMatchNew, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            val writer = BinaryWriter()
            writer.writeMatchV312(packet.match, protocol.slotSize)
            listOf(EncodedPacket(PacketType.BanchoMatchJoinSuccess, writer.toByteArray()))
        }
    )
}
