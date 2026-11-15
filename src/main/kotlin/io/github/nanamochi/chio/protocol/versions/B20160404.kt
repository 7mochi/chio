package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV20151106
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV20160404
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV20151106
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV20160404

/** b20160404 adds a sequence number to the replay frame packet, to allow for replaying frames. */
val B20160404: Protocol =
    B20151107.derive(20160404) {
        decode(PacketType.OsuSpectateFrames) { reader ->
            InboundSpectateFramesPacket(
                readSpectateFramesV20160404(reader) { readScoreFrameV20151106(it) }
            )
        }

        encode(
            PacketEncoder<SpectateFramesPacket> { _, packet ->
                val writer = BinaryWriter()
                writer.writeSpectateFramesV20160404(packet.bundle) {
                    writeScoreFrameV20151106(it)
                }
                listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
            }
        )
    }
