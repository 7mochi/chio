package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchJoinPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchJoin
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV591
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV591

/** b591 implements password protected multiplayer matches. */
val B591: Protocol =
    B558.derive(591) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV591(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV591(reader, slotSize))
    }
    decode(PacketType.OsuMatchJoin) { reader ->
        MatchJoinPacket(MatchJoin(matchId = reader.readInt(), password = reader.readString()))
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV591(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV591(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV591(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV591(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
}

private fun encodeMatchV591(type: PacketType, match: Match, slotSize: Int): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV591(match, slotSize)
    return listOf(EncodedPacket(type, writer.toByteArray()))
}
