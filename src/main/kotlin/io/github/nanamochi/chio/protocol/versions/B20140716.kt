package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.AccountRestrictedPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20140716 sends the restriction status to the client. */
val B20140716: Protocol =
    B20140528.derive(20140716) {
        encodeEmpty<AccountRestrictedPacket>(PacketType.BanchoAccountRestricted)
    }
