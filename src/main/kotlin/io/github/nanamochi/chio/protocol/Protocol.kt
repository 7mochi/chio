package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.MalformedPacketException
import io.github.nanamochi.chio.UnsupportedPacketException
import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.InboundPacket
import io.github.nanamochi.chio.packet.outbound.OutboundPacket
import kotlin.reflect.KClass
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readUShortLe

/** A payload with its shared packet type. The id mapping supplies the raw id. */
data class EncodedPacket(val type: PacketType, val payload: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncodedPacket) return false
        return type == other.type && payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int = 31 * type.hashCode() + payload.contentHashCode()
}

data class EncodeOptions(val includeMTime: Boolean = false)

fun interface PacketDecoder<out P : InboundPacket> {
    fun decode(protocol: Protocol, reader: BinaryReader): P
}

fun interface PacketEncoder<in P : OutboundPacket> {
    fun encode(protocol: Protocol, packet: P): List<EncodedPacket>
}

/**
 * Packet rules for one client build.
 *
 * The format, id mapping, decoders and encoders are immutable. A newer build derives from this
 * value and changes only what differs.
 */
class Protocol
internal constructor(
    val version: Int,
    val format: PacketFormat,
    val idMapping: PacketIdMapping,
    val slotSize: Int,
    val formatChatLinks: Boolean,
    val disableCompression: Boolean,
    val autojoinChannels: List<String>,
    internal val decoders: Map<PacketType, PacketDecoder<*>>,
    internal val encoders: Map<KClass<out OutboundPacket>, PacketEncoder<*>>,
) {
    private val headerSize: Int
        get() = format.headerSize

    fun decode(bytes: ByteArray): InboundPacket {
        val buffer = Buffer().apply { write(bytes) }
        return decode(buffer)
    }

    /**
     * Reads one inbound packet from [source].
     *
     * The decoder checks the payload size against [PacketType.maxSize] before reading it. Bytes
     * after the packet stay in [source]. For multiple packets, use [decodeAll] or [decodeSequence].
     */
    fun decode(source: Source): InboundPacket {
        val rawId = source.readUShortLe().toInt()
        val type =
            try {
                idMapping.packetType(rawId)
            } catch (cause: IllegalArgumentException) {
                throw MalformedPacketException("Unknown packet id $rawId (version $version)", cause)
            }

        if (!type.isInbound) {
            throw MalformedPacketException(
                "Packet '${type.name}' is not an inbound packet (version $version)"
            )
        }

        val decoder = decoders[type] ?: throw UnsupportedPacketException(type, version, "inbound")
        val payload = format.read(source, type.maxSize)
        val reader = BinaryReader(Buffer().apply { write(payload) })

        @Suppress("UNCHECKED_CAST")
        return decoder.decode(this, reader)
    }

    fun decodeAll(bytes: ByteArray): List<InboundPacket> {
        val buffer = Buffer().apply { write(bytes) }
        val packets = mutableListOf<InboundPacket>()

        while (buffer.size >= headerSize) {
            packets += decode(buffer)
        }

        if (!buffer.exhausted()) {
            throw MalformedPacketException("Truncated packet header (version $version)")
        }

        return packets
    }

    /**
     * Lazily decodes packets from [source] until it is exhausted.
     *
     * The sequence is tied to [source] and can be consumed once. It doesn't close the source. The
     * caller owns [source]. Wrap an unbuffered source with `buffered()` to avoid a syscall per
     * read.
     */
    fun decodeSequence(source: Source): Sequence<InboundPacket> = generateSequence {
        if (source.exhausted()) null else decode(source)
    }

    /** Reads packets from [source] until it is exhausted. The caller owns [source]. */
    fun decodeAll(source: Source): List<InboundPacket> = decodeSequence(source).toList()

    @JvmOverloads
    fun encode(packet: OutboundPacket, options: EncodeOptions = EncodeOptions()): ByteArray {
        val out = Buffer()
        encodeTo(out, packet, options)
        return out.readByteArray()
    }

    @JvmOverloads
    fun encodeAll(
        packets: Iterable<OutboundPacket>,
        options: EncodeOptions = EncodeOptions(),
    ): ByteArray {
        val out = Buffer()
        encodeAllTo(out, packets, options)
        return out.readByteArray()
    }

    /**
     * Writes [packet] to [sink] without flushing.
     *
     * The caller owns [sink] and closes it when done.
     */
    @JvmOverloads
    fun encodeTo(
        sink: Sink,
        packet: OutboundPacket,
        options: EncodeOptions = EncodeOptions(),
    ) {
        writeTo(sink, packet, options)
    }

    /** Writes several packets to [sink] without flushing. The caller closes it. */
    @JvmOverloads
    fun encodeAllTo(
        sink: Sink,
        packets: Iterable<OutboundPacket>,
        options: EncodeOptions = EncodeOptions(),
    ) {
        packets.forEach { writeTo(sink, it, options) }
    }

    /** Wraps a payload serialized elsewhere. The version format and id mapping still apply. */
    @JvmOverloads
    fun encodeRaw(
        type: PacketType,
        payload: ByteArray,
        options: EncodeOptions = EncodeOptions(),
    ): ByteArray {
        val out = Buffer()
        encodeRawTo(out, type, payload, options)
        return out.readByteArray()
    }

    /** Writes a pre-serialized outbound payload to [sink] without flushing. */
    private fun encodeRawTo(
        sink: Sink,
        type: PacketType,
        payload: ByteArray,
        options: EncodeOptions = EncodeOptions(),
    ) {
        if (!type.isOutbound) {
            throw MalformedPacketException(
                "Packet '${type.name}' is not an outbound packet (version $version)"
            )
        }

        format.write(
            sink,
            idMapping.rawId(type),
            payload,
            disableCompression,
            options.includeMTime,
        )
    }

    internal fun encodedPackets(packet: OutboundPacket): List<EncodedPacket>? {
        @Suppress("UNCHECKED_CAST")
        val encoder = encoders[packet::class] as? PacketEncoder<OutboundPacket>? ?: return null
        return encoder.encode(this, packet)
    }

    private fun writeTo(sink: Sink, packet: OutboundPacket, options: EncodeOptions) {
        val encoded =
            encodedPackets(packet) ?: throw UnsupportedPacketException(null, version, "outbound")

        for ((type, payload) in encoded) {
            if (!type.isOutbound) {
                throw MalformedPacketException(
                    "Packet '${type.name}' is not an outbound packet (version $version)"
                )
            }
            format.write(
                sink,
                idMapping.rawId(type),
                payload,
                disableCompression,
                options.includeMTime,
            )
        }
    }
}
