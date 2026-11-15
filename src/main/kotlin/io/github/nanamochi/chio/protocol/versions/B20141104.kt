package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.MatchAbortPacket
import io.github.nanamochi.chio.packet.outbound.SwitchTournamentServerPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20141104 allows the client to switch to a tournament server and abort a match. */
val B20141104: Protocol =
    B20140731.derive(20141104) {
        encode<SwitchTournamentServerPacket>(PacketType.BanchoSwitchTournamentServer) {
            writeString(it.server)
        }
        encodeEmpty<MatchAbortPacket>(PacketType.BanchoMatchAbort)
    }
