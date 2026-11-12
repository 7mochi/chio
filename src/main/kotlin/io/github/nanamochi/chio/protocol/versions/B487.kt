package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.outbound.ProtocolNegotiationPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b487 adds protocol negotiation. */
val B487: Protocol =
    B470.derive(487) {
        registerOutbound()
    }

private fun ProtocolBuilder.registerOutbound() {
    encode(
        PacketEncoder<ProtocolNegotiationPacket> { protocol, packet ->
            val writer = BinaryWriter()
            writer.writeInt(packet.version ?: protocol.protocolVersion)
            listOf(EncodedPacket(PacketType.BanchoProtocolNegotiation, writer.toByteArray()))
        }
    )
}
