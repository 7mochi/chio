package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.LobbyJoinPacket
import io.github.nanamochi.chio.packet.outbound.LobbyPartPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20121211 deprecates the lobby user list. */
val B20121211: Protocol =
    B20121207.derive(20121211) {
        encode(PacketEncoder<LobbyJoinPacket> { _, _ -> emptyList() })
        encode(PacketEncoder<LobbyPartPacket> { _, _ -> emptyList() })
    }
