package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.PresenceRequestAllPacket
import io.github.nanamochi.chio.packet.inbound.PresenceRequestPacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceBundlePacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceSinglePacket
import io.github.nanamochi.chio.packet.outbound.UserQuitPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceBundleV20121224
import io.github.nanamochi.chio.protocol.encode.encodeUserPresenceSingleV20121224
import io.github.nanamochi.chio.protocol.encode.encodeUserQuitV20121224

/**
 * b20121224 deprecates the irc quit packet, in favor of a unified quit packet. Additionally, it
 * changes the way how user presences are sent to the client, when the client first joins the
 * server, to reduce bandwidth usage.
 */
val B20121224: Protocol =
    B20121221.derive(20121224) {
        decode(PacketType.OsuPresenceRequest) { reader ->
            PresenceRequestPacket(reader.readIntList16().toList())
        }
        decode(PacketType.OsuPresenceRequestAll) { _ -> PresenceRequestAllPacket }

        encode(
            PacketEncoder<UserQuitPacket> { _, packet ->
                encodeUserQuitV20121224(packet)
            }
        )
        encode(
            PacketEncoder<UserPresenceBundlePacket> { _, packet ->
                encodeUserPresenceBundleV20121224(packet)
            }
        )
        encode(
            PacketEncoder<UserPresenceSinglePacket> { _, packet ->
                encodeUserPresenceSingleV20121224(packet)
            }
        )
    }
