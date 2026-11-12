package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchTransferHostRequestPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV402
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV402

/** b402 adds the host id to matches and lets clients request a host change. */
val B402: Protocol =
    B388.derive(402) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV402(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV402(reader, slotSize))
    }
    decode(PacketType.OsuMatchTransferHost) { reader ->
        MatchTransferHostRequestPacket(reader.readInt())
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV402(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV402(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                encodeMatchV402(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV402(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
}

private fun encodeMatchV402(type: PacketType, match: Match, slotSize: Int): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV402(match, slotSize)
    return listOf(EncodedPacket(type, writer.toByteArray()))
}
