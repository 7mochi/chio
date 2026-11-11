package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.LobbyJoinPacket as InboundLobbyJoinPacket
import io.github.nanamochi.chio.packet.inbound.LobbyPartPacket as InboundLobbyPartPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSettingsPacket
import io.github.nanamochi.chio.packet.inbound.MatchChangeSlotPacket
import io.github.nanamochi.chio.packet.inbound.MatchCreatePacket
import io.github.nanamochi.chio.packet.inbound.MatchJoinPacket
import io.github.nanamochi.chio.packet.inbound.MatchLockPacket
import io.github.nanamochi.chio.packet.inbound.MatchPartPacket
import io.github.nanamochi.chio.packet.inbound.MatchReadyPacket
import io.github.nanamochi.chio.packet.model.MatchJoin
import io.github.nanamochi.chio.packet.outbound.FellowSpectatorJoinedPacket
import io.github.nanamochi.chio.packet.outbound.FellowSpectatorLeftPacket
import io.github.nanamochi.chio.packet.outbound.LobbyJoinPacket as OutboundLobbyJoinPacket
import io.github.nanamochi.chio.packet.outbound.LobbyPartPacket as OutboundLobbyPartPacket
import io.github.nanamochi.chio.packet.outbound.MatchDisbandPacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinFailPacket
import io.github.nanamochi.chio.packet.outbound.MatchJoinSuccessPacket
import io.github.nanamochi.chio.packet.outbound.MatchNewPacket
import io.github.nanamochi.chio.packet.outbound.MatchUpdatePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readMatchV298
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMatchV298

/** b298 adds a partial multiplayer implementation and fellow spectators. */
val B298: Protocol =
    B296.derive(298) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuLobbyPart) { _ -> InboundLobbyPartPacket }
    decode(PacketType.OsuLobbyJoin) { _ -> InboundLobbyJoinPacket }
    decode(PacketType.OsuMatchPart) { _ -> MatchPartPacket }
    decode(PacketType.OsuMatchReady) { _ -> MatchReadyPacket }
    decode(PacketType.OsuMatchCreate) { reader ->
        MatchCreatePacket(readMatchV298(reader, slotSize))
    }
    decode(PacketType.OsuMatchChangeSettings) { reader ->
        MatchChangeSettingsPacket(readMatchV298(reader, slotSize))
    }
    decode(PacketType.OsuMatchJoin) { reader ->
        MatchJoinPacket(MatchJoin(matchId = reader.readInt()))
    }
    decode(PacketType.OsuMatchLock) { reader -> MatchLockPacket(reader.readInt()) }
    decode(PacketType.OsuMatchChangeSlot) { reader -> MatchChangeSlotPacket(reader.readInt()) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<MatchUpdatePacket> { _, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMatchV298(packet.match)
                listOf(EncodedPacket(PacketType.BanchoMatchUpdate, writer.toByteArray()))
            }
        }
    )
    encode(
        PacketEncoder<MatchNewPacket> { _, packet ->
            if (packet.match.id > 0xFF) {
                emptyList()
            } else {
                val writer = BinaryWriter()
                writer.writeMatchV298(packet.match)
                listOf(EncodedPacket(PacketType.BanchoMatchNew, writer.toByteArray()))
            }
        }
    )
    encode<MatchDisbandPacket>(PacketType.BanchoMatchDisband) {
        writeInt(it.matchId)
    }
    encode<OutboundLobbyJoinPacket>(PacketType.BanchoLobbyJoin) {
        writeInt(it.userId)
    }
    encode<OutboundLobbyPartPacket>(PacketType.BanchoLobbyPart) {
        writeInt(it.userId)
    }
    encode(
        PacketEncoder<MatchJoinSuccessPacket> { _, packet ->
            val writer = BinaryWriter()
            writer.writeMatchV298(packet.match)
            listOf(EncodedPacket(PacketType.BanchoMatchJoinSuccess, writer.toByteArray()))
        }
    )
    encodeEmpty<MatchJoinFailPacket>(PacketType.BanchoMatchJoinFail)
    encode<FellowSpectatorJoinedPacket>(PacketType.BanchoFellowSpectatorJoined) {
        writeInt(it.userId)
    }
    encode<FellowSpectatorLeftPacket>(PacketType.BanchoFellowSpectatorLeft) {
        writeInt(it.userId)
    }
}
