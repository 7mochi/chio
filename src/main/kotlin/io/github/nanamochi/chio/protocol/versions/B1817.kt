package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.InvitePacket as InboundInvitePacket
import io.github.nanamochi.chio.packet.outbound.InvitePacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV320

/** b1817 adds the ability to send multiplayer match invites to other users. */
val B1817: Protocol =
    B1800.derive(1817) {
        decode(PacketType.OsuInvite) { reader -> InboundInvitePacket(reader.readInt()) }

        encode(
            PacketEncoder<InvitePacket> { protocol, packet ->
                val writer = BinaryWriter()
                writer.writeMessageV320(packet.message, protocol.formatChatLinks)
                listOf(EncodedPacket(PacketType.BanchoInvite, writer.toByteArray()))
            }
        )
    }
