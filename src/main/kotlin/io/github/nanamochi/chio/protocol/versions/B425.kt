package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.LoginPermissionsPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b425 sends the user's permissions during login. */
val B425: Protocol =
    B402.derive(425) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode<LoginPermissionsPacket>(PacketType.BanchoLoginPermissions) {
        writeInt(it.permissions.bits)
    }
}
