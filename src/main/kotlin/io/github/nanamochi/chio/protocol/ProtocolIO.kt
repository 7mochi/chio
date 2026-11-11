@file:JvmName("ProtocolIO")

package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.packet.inbound.InboundPacket
import io.github.nanamochi.chio.packet.outbound.OutboundPacket
import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * Reads one inbound packet from this source without closing it. Wrap unbuffered sources with
 * `buffered()`.
 */
fun Source.readBanchoPacket(protocol: Protocol): InboundPacket = protocol.decode(this)

/** Writes one packet without flushing. The caller owns and closes the sink. */
@JvmOverloads
fun Sink.writeBanchoPacket(
    protocol: Protocol,
    packet: OutboundPacket,
    options: EncodeOptions = EncodeOptions(),
): Unit = protocol.encodeTo(this, packet, options)

/** Writes several packets without flushing. The caller closes the sink. */
@JvmOverloads
fun Sink.writeBanchoPackets(
    protocol: Protocol,
    packets: Iterable<OutboundPacket>,
    options: EncodeOptions = EncodeOptions(),
): Unit = protocol.encodeAllTo(this, packets, options)
