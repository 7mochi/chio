package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.TournamentJoinMatchChannelPacket
import io.github.nanamochi.chio.packet.inbound.TournamentLeaveMatchChannelPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20151107 adds tournament client chat support. */
val B20151107: Protocol =
    B20151106.derive(20151107) {
        decode(PacketType.OsuTournamentJoinMatchChannel) { reader ->
            TournamentJoinMatchChannelPacket(reader.readInt())
        }
        decode(PacketType.OsuTournamentLeaveMatchChannel) { reader ->
            TournamentLeaveMatchChannelPacket(reader.readInt())
        }
    }
