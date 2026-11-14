package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.MatchPlayerSkippedPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b1365 now sends "player skipped" messages in multiplayer. */
val B1365: Protocol =
    B1183.derive(1365) {
        encode(
            PacketEncoder<MatchPlayerSkippedPacket> { protocol, packet ->
                if (packet.slotId >= protocol.slotSize) {
                    emptyList()
                } else {
                    val writer = BinaryWriter()
                    writer.writeUInt(packet.slotId)
                    listOf(EncodedPacket(PacketType.BanchoMatchPlayerSkipped, writer.toByteArray()))
                }
            }
        )
    }
