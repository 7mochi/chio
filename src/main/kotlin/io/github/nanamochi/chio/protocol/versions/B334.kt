package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangeBeatmapPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeModsPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchFailedPacket
import io.github.nanamochi.chio.packet.inbound.MatchLoadCompletePacket
import io.github.nanamochi.chio.packet.inbound.MatchNoBeatmapPacket
import io.github.nanamochi.chio.packet.inbound.MatchNotReadyPacket
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.MatchAllPlayersLoadedPacket
import io.github.nanamochi.chio.packet.outbound.MatchCompletePacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchPlayerFailedPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket as OutboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket as OutboundMatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchTransferHostPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.protocol.CompressionFlagPacketFormat
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.PacketIdMappingV334
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV334
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV334
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV294
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV334
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV334
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV294

/**
 * b334 introduces the compression flag, the match mods field, per-slot status bytes, checksum-less
 * score frames and packet ids 50–58.
 */
val B334: Protocol =
    B323.derive(334) {
        format = CompressionFlagPacketFormat(150)
        idMapping = PacketIdMappingV334
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchChangeMods) { reader ->
        MatchChangeModsPacket(Mods.fromBits(reader.readInt()))
    }
    decode(PacketType.OsuMatchLoadComplete) { _ -> MatchLoadCompletePacket }
    decode(PacketType.OsuMatchNoBeatmap) { _ -> MatchNoBeatmapPacket }
    decode(PacketType.OsuMatchNotReady) { _ -> MatchNotReadyPacket }
    decode(PacketType.OsuMatchFailed) { _ -> MatchFailedPacket }
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV334(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV334(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeBeatmap) { reader ->
        MatchChangeBeatmapPacket(readMatchV334(reader, slotSize))
    }
    decode(PacketType.OsuMatchScoreUpdate) { reader ->
        InboundMatchScoreUpdatePacket(readScoreFrameV334(reader))
    }
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV294(reader) { readScoreFrameV334(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encodeEmpty<MatchTransferHostPacket>(PacketType.BanchoMatchTransferHost)
    encodeEmpty<MatchAllPlayersLoadedPacket>(PacketType.BanchoMatchAllPlayersLoaded)
    encode<MatchPlayerFailedPacket>(PacketType.BanchoMatchPlayerFailed) {
        writeInt(it.userId)
    }
    encodeEmpty<MatchCompletePacket>(PacketType.BanchoMatchComplete)
    encode(
        PacketEncoder<OutboundMatchStartPacket> { protocol, packet ->
            val writer = BinaryWriter()
            writer.writeMatchV334(packet.match, protocol.slotSize)
            listOf(EncodedPacket(PacketType.BanchoMatchStart, writer.toByteArray()))
        }
    )
    encode(
        PacketEncoder<OutboundMatchScoreUpdatePacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeScoreFrameV334(packet.frame)
            listOf(EncodedPacket(PacketType.BanchoMatchScoreUpdate, writer.toByteArray()))
        }
    )
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV294(packet.bundle) { writeScoreFrameV334(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMatchV334(packet.match, protocol.slotSize)
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
                writer.writeMatchV334(packet.match, protocol.slotSize)
                listOf(EncodedPacket(PacketType.BanchoMatchNew, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            val writer = BinaryWriter()
            writer.writeMatchV334(packet.match, protocol.slotSize)
            listOf(EncodedPacket(PacketType.BanchoMatchJoinSuccess, writer.toByteArray()))
        }
    )
}
