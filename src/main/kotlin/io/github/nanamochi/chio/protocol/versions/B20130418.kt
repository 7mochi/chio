package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangePasswordPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV20130418
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV535
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV20130418
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV20130418
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV535
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV20130418

/** b20130418 implements the mania random mod, which requires the "seed" field to be sent. */
val B20130418: Protocol =
    B20130303.derive(20130418) {
        protocolVersion = 18
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV20130418(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV20130418(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangePassword) { reader ->
        MatchChangePasswordPacket(readMatchV20130418(reader, slotSize))
    }
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV20130418(reader) { readScoreFrameV535(it) })
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV20130418(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130418(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130418(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV20130418(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<SpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV20130418(packet.bundle) { writeScoreFrameV535(it) }
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
}

private fun encodeMatchV20130418(
    type: PacketType,
    match: Match,
    slotSize: Int,
): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV20130418(match, slotSize)

    return listOf(EncodedPacket(type, writer.toByteArray()))
}
