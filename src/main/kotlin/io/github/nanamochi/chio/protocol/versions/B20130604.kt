package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.SwitchServerPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/**
 * b20130604 allows for forced bancho server switching, after the client was idle for a certain
 * amount of time.
 */
val B20130604: Protocol =
    B20130509.derive(20130604) {
        encode<SwitchServerPacket>(PacketType.BanchoSwitchServer) {
            writeInt(it.afterIdleTime)
        }
    }
