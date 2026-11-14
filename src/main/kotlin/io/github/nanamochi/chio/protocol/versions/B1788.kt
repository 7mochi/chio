package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.ExitPacket
import io.github.nanamochi.chio.packet.inbound.UserStatsRequestPacket
import io.github.nanamochi.chio.packet.inbound.UserStatusPacket
import io.github.nanamochi.chio.packet.outbound.FriendsListPacket
import io.github.nanamochi.chio.packet.outbound.IrcJoinPacket
import io.github.nanamochi.chio.packet.outbound.RestartPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.PacketIdMappingV1788
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.decode.readUserStatusV1788
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV1788
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV1788

/**
 * b1788 includes probably the largest amount of changes to the bancho protocol overall.
 * - bUserStats split up into [UserPresencePacket] and [UserStatsPacket]
 * - Server no longer needs to send bUserStats to clients, instead the client requests it themselves
 * - The `count` datatype for bListInt got changed into a signed 16-bit integer
 * - The user status no longer contains a boolean for updates
 * - [IrcJoinPacket] is now deprecated, in favor of user IDs being negative if they are IRC users
 * - [RestartPacket] informs the client that bancho is restarting
 * - [ExitPacket] indicates if the client is exiting because of an update
 */
val B1788: Protocol =
    B1600.derive(1788) {
        protocolVersion = 6
        idMapping = PacketIdMappingV1788
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuUserStatus) { reader ->
        UserStatusPacket(readUserStatusV1788(reader))
    }
    decode(PacketType.OsuUserStatsRequest) { reader ->
        UserStatsRequestPacket(reader.readIntList16().toList())
    }
    decode(PacketType.OsuExit) { reader -> ExitPacket(updating = reader.readInt() == 1) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<UserStatsPacket> { _, packet ->
            encodeUserStatsV1788(packet)
        }
    )
    encode(
        PacketEncoder<UserPresencePacket> { _, packet ->
            encodeUserPresenceV1788(packet)
        }
    )
    encode<FriendsListPacket>(PacketType.BanchoFriendsList) {
        writeIntList16(it.friends.toIntArray())
    }
    encode<RestartPacket>(PacketType.BanchoRestart) { writeUInt(it.retryAfterMs) }
    encode(PacketEncoder<IrcJoinPacket> { _, _ -> emptyList() })
}
