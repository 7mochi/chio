package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.MatchScoreUpdatePacket as InboundMatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.inbound.SpectateFramesPacket as InboundSpectateFramesPacket
import io.github.nanamochi.chio.packet.outbound.MatchScoreUpdatePacket
import io.github.nanamochi.chio.packet.outbound.SpectateFramesPacket
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.PacketEncoder
import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.decode.readScoreFrameV20151106
import io.github.nanamochi.chio.protocol.decode.readSpectateFramesV20130418
import io.github.nanamochi.chio.protocol.derive
import io.github.nanamochi.chio.protocol.encode.writeScoreFrameV20151106
import io.github.nanamochi.chio.protocol.encode.writeSpectateFramesV20130418

/** b20151106 adds scorev2-specific fields to the scoreframe packet. */
val B20151106: Protocol =
    B20150915.derive(20151106) {
        decode(PacketType.OsuMatchScoreUpdate) { reader ->
            InboundMatchScoreUpdatePacket(readScoreFrameV20151106(reader))
        }
        decode(PacketType.OsuSpectateFrames) { reader ->
            InboundSpectateFramesPacket(
                readSpectateFramesV20130418(reader) { readScoreFrameV20151106(it) }
            )
        }

        encode(
            PacketEncoder<MatchScoreUpdatePacket> { protocol, packet ->
                if (packet.frame.id >= protocol.slotSize) {
                    emptyList()
                } else {
                    val writer = BinaryWriter()
                    writer.writeScoreFrameV20151106(packet.frame)
                    listOf(EncodedPacket(PacketType.BanchoMatchScoreUpdate, writer.toByteArray()))
                }
            }
        )
        encode(
            PacketEncoder<SpectateFramesPacket> { _, packet ->
                val writer = BinaryWriter()
                writer.writeSpectateFramesV20130418(packet.bundle) {
                    writeScoreFrameV20151106(it)
                }
                listOf(EncodedPacket(PacketType.BanchoSpectateFrames, writer.toByteArray()))
            }
        )
    }
