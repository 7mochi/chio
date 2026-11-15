package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.VersionUpdateForcedPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/**
 * b20130509 reworks the updating system, and can now differentiate between forced updates and
 * regular updates.
 */
val B20130509: Protocol =
    B20130418.derive(20130509) {
        encodeEmpty<VersionUpdateForcedPacket>(PacketType.BanchoVersionUpdateForced)
    }
