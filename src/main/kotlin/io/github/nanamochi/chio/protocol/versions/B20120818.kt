package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangePasswordPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchStartPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV20120818
import io.github.nanamochi.chio.protocol.decode.readUserStatusV20120818
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV20120818
import io.github.nanamochi.chio.protocol.encode.writeMatchV20120818

/** b20120818 changes the "mods" datatype from s16 to u32. */
val B20120818: Protocol =
    B20120806.derive(20120818) {
        protocolVersion = 11
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuUserStatus) { reader ->
        UserStatusPacket(readUserStatusV20120818(reader))
    }
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV20120818(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV20120818(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangePassword) { reader ->
        MatchChangePasswordPacket(readMatchV20120818(reader, slotSize))
    }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { _, packet ->
            encodeUserStatsV20120818(packet)
        }
    )
    encode(
        PacketEncoder<MatchStartPacket> { protocol, packet ->
            encodeMatchV20120818(PacketType.BanchoMatchStart, packet.match, protocol.slotSize)
        }
    )
    encode(
        PacketEncoder<MatchUpdatePacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20120818(PacketType.BanchoMatchUpdate, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { protocol, packet ->
            if (packet.match.id > 0xFFFF) {
                emptyList()
            } else {
                encodeMatchV20120818(PacketType.BanchoMatchNew, packet.match, protocol.slotSize)
            }
        }
    )
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { protocol, packet ->
            encodeMatchV20120818(PacketType.BanchoMatchJoinSuccess, packet.match, protocol.slotSize)
        }
    )
}

private fun encodeMatchV20120818(
    type: PacketType,
    match: Match,
    slotSize: Int,
): List<EncodedPacket> {
    val writer = BinaryWriter()
    writer.writeMatchV20120818(match, slotSize)

    return listOf(EncodedPacket(type, writer.toByteArray()))
}
