package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchChangePasswordPacket
import io.github.nanamochi.chio.packet.outbound.MatchChangePasswordPacket as OutboundMatchChangePasswordPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.decode.readMatchV1796
import io.github.nanamochi.chio.protocol.derive

/** b20120703 introduces a new packet to change match passwords. */
val B20120703: Protocol =
    B20120518.derive(20120703) {
        decode(PacketType.OsuMatchChangePassword) { reader ->
            MatchChangePasswordPacket(readMatchV1796(reader, slotSize))
        }
        encode<OutboundMatchChangePasswordPacket>(PacketType.BanchoMatchChangePassword) {
            writeString(it.password)
        }
    }
