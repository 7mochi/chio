package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.ChangeFriendOnlyDmsPacket
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.outbound.UserDmsBlockedPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeMessageV20121225

/** b20130121 allows users to block private messages from non-friends. */
val B20130131: Protocol =
    B20130118.derive(20130131) {
        decode(PacketType.OsuChangeFriendOnlyDms) { reader ->
            ChangeFriendOnlyDmsPacket(reader.readInt() == 1)
        }

        encode(
            PacketEncoder<UserDmsBlockedPacket> { protocol, packet ->
                val writer = BinaryWriter()
                writer.writeMessageV20121225(
                    Message(sender = "", content = "", target = packet.username),
                    protocol.formatChatLinks,
                )
                listOf(EncodedPacket(PacketType.BanchoUserDmsBlocked, writer.toByteArray()))
            }
        )
    }
