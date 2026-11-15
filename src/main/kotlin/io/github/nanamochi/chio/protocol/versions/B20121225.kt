package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MessagePacket as InboundMessagePacket
import io.github.nanamochi.chio.packet.inbound.PrivateMessagePacket
import io.github.nanamochi.chio.packet.outbound.InvitePacket
import io.github.nanamochi.chio.packet.outbound.MessagePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.decode.readMessageV20121225
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV20121225

/**
 * b20121225 adds the user ID of the message sender to the message packet, so that the client can
 * request the user's presence, if it's not available.
 */
val B20121225: Protocol =
    B20121224.derive(20121225) {
        protocolVersion = 15
        decode(PacketType.OsuMessage) { reader ->
            InboundMessagePacket(readMessageV20121225(reader))
        }
        decode(PacketType.OsuPrivateMessage) { reader ->
            PrivateMessagePacket(readMessageV20121225(reader))
        }

        encode(
            PacketEncoder<MessagePacket> { protocol, packet ->
                val writer = BinaryWriter()
                writer.writeMessageV20121225(packet.message, protocol.formatChatLinks)
                listOf(EncodedPacket(PacketType.BanchoMessage, writer.toByteArray()))
            }
        )
        encode(
            PacketEncoder<InvitePacket> { protocol, packet ->
                val writer = BinaryWriter()
                writer.writeMessageV20121225(packet.message, protocol.formatChatLinks)
                listOf(EncodedPacket(PacketType.BanchoInvite, writer.toByteArray()))
            }
        )
    }
