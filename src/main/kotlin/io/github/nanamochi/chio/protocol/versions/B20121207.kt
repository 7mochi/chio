package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.TournamentMatchInfoPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20121207 allows tourney clients to request multiplayer match information. */
val B20121207: Protocol =
    B20121203.derive(20121207) {
        decode(PacketType.OsuTournamentMatchInfo) { reader ->
            TournamentMatchInfoPacket(reader.readInt())
        }
    }
