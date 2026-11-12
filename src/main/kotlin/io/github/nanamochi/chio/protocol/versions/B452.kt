package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.FriendsAddPacket
import io.github.nanamochi.chio.packet.inbound.FriendsRemovePacket
import io.github.nanamochi.chio.packet.outbound.FriendsListPacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceV452

/** b452 adds friend management and permissions to user presences. */
val B452: Protocol =
    B425.derive(452) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuFriendsAdd) { reader -> FriendsAddPacket(reader.readInt()) }
    decode(PacketType.OsuFriendsRemove) { reader -> FriendsRemovePacket(reader.readInt()) }
}

private fun ProtocolBuilder.registerOutbound() {
    encode<FriendsListPacket>(PacketType.BanchoFriendsList) {
        writeIntList32(it.friends.toIntArray())
    }
    encode(
        PacketEncoder<UserPresencePacket> { protocol, packet ->
            encodeUserPresenceV452(protocol, packet)
        }
    )
}
