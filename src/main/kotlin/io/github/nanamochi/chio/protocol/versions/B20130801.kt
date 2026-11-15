package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.LoginReplyPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

private const val INACTIVE_LOGIN_REPLY = -4
private const val INVALID_LOGIN_REPLY = -1

private const val INACTIVE_ACCOUNT_MESSAGE =
    "Your account is not yet activated. Please check your email for activation instructions!"

/**
 * b20130801 deprecates the "user is inactive" error, and is now interpreted as a "user is banned"
 * error by clients. Chio will try to account for this by sending an announce packet with the
 * original message.
 */
val B20130801: Protocol =
    B20130604.derive(20130801) {
        encode(
            PacketEncoder<LoginReplyPacket> { _, packet ->
                if (packet.reply == INACTIVE_LOGIN_REPLY) {
                    val announce = BinaryWriter()
                    announce.writeString(INACTIVE_ACCOUNT_MESSAGE)
                    val reply = BinaryWriter()
                    reply.writeInt(INVALID_LOGIN_REPLY)

                    listOf(
                        EncodedPacket(
                            PacketType.BanchoAnnounce,
                            announce.toByteArray(),
                        ),
                        EncodedPacket(PacketType.BanchoLoginReply, reply.toByteArray()),
                    )
                } else {
                    val reply = BinaryWriter()
                    reply.writeInt(packet.reply)
                    listOf(EncodedPacket(PacketType.BanchoLoginReply, reply.toByteArray()))
                }
            }
        )
    }
