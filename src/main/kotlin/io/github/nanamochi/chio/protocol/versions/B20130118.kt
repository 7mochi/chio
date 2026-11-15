package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangePasswordPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV20130118
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV20130118

/** b20130118 adds freemod support to multiplayer matches. */
val B20130118: Protocol =
    B20121225.derive(20130118) {
        protocolVersion = 16
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV20130118(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV20130118(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangePassword) { reader ->
        MatchChangePasswordPacket(readMatchV20130118(reader, slotSize))
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV20130118(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130118(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130118(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV20130118(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
}

private fun encodeMatchV20130118(
    type: PacketType,
    match: Match,
    slotSize: Int,
): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV20130118(match, slotSize)

    return listOf(EncodedPacket(type, writer.toByteArray()))
}
