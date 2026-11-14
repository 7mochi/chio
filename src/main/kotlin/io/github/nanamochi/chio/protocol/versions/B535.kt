package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket as OutboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.TitleUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV535
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV535
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV374
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV535
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV535
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV374

/** b535 adds the scoring and team types for multiplayer matches, as well as the tag multiplayer game mode */
val B535: Protocol =
    B504.derive(535) {
        protocolVersion = 3
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV535(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV535(reader, slotSize))
    }
    decode(PacketType.OsuMatchScoreUpdate) { reader ->
        InboundMatchScoreUpdatePacket(readScoreFrameV535(reader))
    }
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV374(reader) { readScoreFrameV535(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encodeEmpty<TitleUpdatePacket>(PacketType.BanchoTitleUpdate)
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV535(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV535(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV535(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV535(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<OutboundMatchScoreUpdatePacket> { protocol, packet ->
            if (packet.frame.id >= protocol.slotSize) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeScoreFrameV535(packet.frame)
                listOf(EncodedPacket(PacketType.BanchoMatchScoreUpdate, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV374(packet.bundle) { writeScoreFrameV535(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
}

private fun encodeMatchV535(type: PacketType, match: Match, slotSize: Int): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV535(match, slotSize)

    return listOf(EncodedPacket(type, writer.toByteArray()))
}
