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
import io.github.nanamochi.chio.protocol.decode.readMatchV20130303
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV20130303

/** b20130303 adds the actual freemod implementation. */
val B20130303: Protocol =
    B20130209.derive(20130303) {
        protocolVersion = 17
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV20130303(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV20130303(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangePassword) { reader ->
        MatchChangePasswordPacket(readMatchV20130303(reader, slotSize))
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV20130303(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130303(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20130303(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV20130303(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
}

private fun encodeMatchV20130303(
    type: PacketType,
    match: Match,
    slotSize: Int,
): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV20130303(match, slotSize)

    return listOf(EncodedPacket(type, writer.toByteArray()))
}
