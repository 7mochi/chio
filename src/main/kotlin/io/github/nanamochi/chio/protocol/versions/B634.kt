package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.ReceiveUpdatesPacket
import io.github.nanamochi.chio.packet.outbound.MonitorPacket
import io.github.nanamochi.chio.packet.types.PresenceFilter
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b634 adds the monitor and player update request packets. */
val B634: Protocol =
    B613.derive(634) {
        decode(PacketType.OsuReceiveUpdates) { reader ->
            ReceiveUpdatesPacket(PresenceFilter.from(reader.readInt()))
        }
        encodeEmpty<MonitorPacket>(PacketType.BanchoMonitor)
    }
