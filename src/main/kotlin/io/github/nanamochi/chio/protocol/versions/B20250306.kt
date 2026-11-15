package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.encodeUserStatsV20250306

/**
 * b20250306 changes the pp datatype from s16 to u16, due to mrekk exceeding the integer limit.
 * https://osu.ppy.sh/home/changelog/stable40/20250306.2
 * https://www.reddit.com/r/osugame/comments/1j3y7fh/
 */
val B20250306: Protocol =
    B20161101.derive(20250306) {
        encode(
            PacketEncoder<UserStatsPacket> { _, packet ->
                encodeUserStatsV20250306(packet)
            }
        )
    }
