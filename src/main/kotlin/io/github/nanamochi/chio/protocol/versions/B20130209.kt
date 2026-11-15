package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.outbound.TargetIsSilencedPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV20121225

/**
 * b20130209 adds the target silenced packet, which informs the client, that a user has been
 * silenced.
 */
val B20130209: Protocol =
    B20130131.derive(20130209) {
        encode(
            PacketEncoder<TargetIsSilencedPacket> { protocol, packet ->
                val writer = BinaryWriter()
                writer.writeMessageV20121225(
                    Message(sender = "", content = "", target = packet.username),
                    protocol.formatChatLinks,
                )
                listOf(EncodedPacket(PacketType.BanchoTargetIsSilenced, writer.toByteArray()))
            }
        )
    }
