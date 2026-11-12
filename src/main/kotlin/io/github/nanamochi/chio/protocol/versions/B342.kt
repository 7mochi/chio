package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchSkipRequestPacket
import io.github.nanamochi.chio.packet.outbound.MatchSkipPacket
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.ProtocolBuilder
import io.github.nanamochi.chio.protocol.derive

/** b342 adds the multiplayer skip packets. */
val B342: Protocol =
    B340.derive(342) {
        registerInbound()
        registerOutbound()
    }

private fun ProtocolBuilder.registerInbound() {
    decode(PacketType.OsuMatchSkipRequest) { _ -> MatchSkipRequestPacket }
}

private fun ProtocolBuilder.registerOutbound() {
    encodeEmpty<MatchSkipPacket>(PacketType.BanchoMatchSkip)
}
