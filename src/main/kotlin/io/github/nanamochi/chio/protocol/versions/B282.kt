package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.CantSpectatePacket
import io.github.nanamochi.chio.packet.inbound.ErrorReportPacket
import io.github.nanamochi.chio.packet.inbound.ExitPacket
import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.inbound.PongPacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.inbound.StartSpectatingPacket
import io.github.nanamochi.chio.packet.inbound.StatusUpdateRequestPacket
import io.github.nanamochi.chio.packet.inbound.StopSpectatingPacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.outbound.InvitePacket
import io.github.nanamochi.chio.packet.outbound.IrcChangeUsernamePacket
import io.github.nanamochi.chio.packet.outbound.IrcJoinPacket
import io.github.nanamochi.chio.packet.outbound.IrcQuitPacket
import io.github.nanamochi.chio.packet.outbound.LoginReplyPacket
import io.github.nanamochi.chio.packet.outbound.MessagePacket as OutboundMessagePacket
import io.github.nanamochi.chio.packet.outbound.PingPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket as OutboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.SpectatorCantSpectatePacket
import io.github.nanamochi.chio.packet.outbound.SpectatorJoinedPacket
import io.github.nanamochi.chio.packet.outbound.SpectatorLeftPacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceBundlePacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceSinglePacket
import io.github.nanamochi.chio.packet.outbound.UserQuitPacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.outbound.VersionUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.LegacyPacketFormat
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.PacketIdMappingV282
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readChannelMessageV282
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV282
import io.github.nanamochi.chio.protocol.decode.readUserStatusV282
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceBundle
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceSingle
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV282
import io.github.nanamochi.chio.protocol.encode.encodeUserQuit
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV282
import io.github.nanamochi.chio.protocol.encode.writeMessageV282
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV282
import io.github.nanamochi.chio.protocol.protocol

/** Base implementation of the Bancho protocol. Every later version derives from this one. */
val B282: Protocol =
    protocol(282) {
        format = LegacyPacketFormat
        idMapping = PacketIdMappingV282
        slotSize = 8
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuUserStatus) { reader -> UserStatusPacket(readUserStatusV282(reader)) }
    decode(PacketType.OsuMessage) { reader ->
        InboundMessagePacket(readChannelMessageV282(reader))
    }
    decode(PacketType.OsuExit) { ExitPacket }
    decode(PacketType.OsuStartSpectating) { reader -> StartSpectatingPacket(reader.readInt()) }
    decode(PacketType.OsuSpectateFrames) { reader ->
        InboundSpectateFramesPacket(readSpectateFramesV282(reader))
    }
    decode(PacketType.OsuErrorReport) { reader -> ErrorReportPacket(reader.readString()) }
    decode(PacketType.OsuPong) { PongPacket }
    decode(PacketType.OsuStatusUpdateRequest) { StatusUpdateRequestPacket }
    decode(PacketType.OsuStopSpectating) { StopSpectatingPacket }
    decode(PacketType.OsuCantSpectate) { CantSpectatePacket }
}

private fun ProtocolBuilder.registerOutbound() {
    encode<LoginReplyPacket>(PacketType.BanchoLoginReply) { writeInt(it.reply) }
    encodeEmpty<PingPacket>(PacketType.BanchoPing)
    encode(
        PacketEncoder<OutboundMessagePacket> { protocol, packet ->
            val message = packet.message
            if (message.target !in protocol.autojoinChannels) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMessageV282(message, protocol.formatChatLinks)
                listOf(EncodedPacket(PacketType.BanchoMessage, writer.toByteArray()))
            }
        }
    )
    encode<IrcChangeUsernamePacket>(PacketType.BanchoIrcChangeUsername) {
        writeString("${it.oldName}>>>>${it.newName}")
    }
    encode(
        PacketEncoder<UserStatsPacket> { protocol, packet ->
            encodeUserStatsV282(protocol, packet)
        }
    )
    encode<IrcJoinPacket>(PacketType.BanchoIrcJoin) { writeString(it.name) }
    encode<IrcQuitPacket>(PacketType.BanchoIrcQuit) { writeString(it.name) }
    encode<SpectatorJoinedPacket>(PacketType.BanchoSpectatorJoined) {
        writeUInt(it.userId)
    }
    encode<SpectatorLeftPacket>(PacketType.BanchoSpectatorLeft) {
        writeUInt(it.userId)
    }
    encode(
        PacketEncoder<OutboundSpectateFramesPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeSpectateFramesV282(packet.bundle)
            listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
        }
    )
    encodeEmpty<VersionUpdatePacket>(PacketType.BanchoVersionUpdate)
    encode<SpectatorCantSpectatePacket>(PacketType.BanchoSpectatorCantSpectate) {
        writeUInt(it.userId)
    }
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV282(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresenceSinglePacket> { protocol, packet ->
            encodeUserPresenceSingle(protocol, packet)
        }
    )
    encode(
        PacketEncoder<UserPresenceBundlePacket> { protocol, packet ->
            encodeUserPresenceBundle(protocol, packet)
        }
    )
    encode(
        PacketEncoder<InvitePacket> { protocol, packet ->
            protocol.encodedPackets(OutboundMessagePacket(packet.message)).orEmpty()
        }
    )
    encode(PacketEncoder<UserQuitPacket> { protocol, packet -> encodeUserQuit(protocol, packet) })
}
