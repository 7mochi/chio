package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.binary.BinaryReader
import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.inbound.InboundPacket
import io.github.nanamochi.chio.packet.outbound.OutboundPacket
import java.util.function.BiConsumer
import java.util.function.BiFunction
import kotlin.reflect.KClass

@DslMarker annotation class ProtocolDsl

fun protocol(version: Int, block: ProtocolBuilder.() -> Unit): Protocol =
    ProtocolBuilder(version).apply(block).build()

/** Creates a new protocol from this one and applies [block]. This protocol stays unchanged. */
fun Protocol.derive(version: Int, block: ProtocolBuilder.() -> Unit): Protocol =
    ProtocolBuilder(version, this).apply(block).build()

/** Mutable configuration for [protocol] and [derive]. [build] copies the current values. */
@ProtocolDsl
class ProtocolBuilder @JvmOverloads internal constructor(val version: Int, base: Protocol? = null) {
    var protocolVersion: Int = base?.protocolVersion ?: 0
    var format: PacketFormat = base?.format ?: LegacyPacketFormat
    var idMapping: PacketIdMapping = base?.idMapping ?: PacketIdMappingV282
    var slotSize: Int = base?.slotSize ?: 8
    var formatChatLinks: Boolean = base?.formatChatLinks ?: true
    var disableCompression: Boolean = base?.disableCompression ?: false
    var autojoinChannels: List<String> = base?.autojoinChannels ?: listOf("#osu", "#announce")

    private val decoderOverrides: MutableMap<PacketType, PacketDecoder<*>> =
        base?.decoders?.toMutableMap() ?: mutableMapOf()

    private val encoderOverrides: MutableMap<KClass<out OutboundPacket>, PacketEncoder<*>> =
        base?.encoders?.toMutableMap() ?: mutableMapOf()

    @JvmSynthetic
    fun <P : InboundPacket> decode(type: PacketType, decoder: PacketDecoder<P>) {
        require(type.isInbound) { "Packet '${type.name}' is not an inbound packet" }
        decoderOverrides[type] = decoder
    }

    @JvmSynthetic
    fun <P : InboundPacket> decode(type: PacketType, read: (BinaryReader) -> P) {
        require(type.isInbound) { "Packet '${type.name}' is not an inbound packet" }
        decoderOverrides[type] = PacketDecoder { _, reader -> read(reader) }
    }

    /** Registers a decoder with access to the protocol and reader. */
    fun <P : InboundPacket> decode(
        type: PacketType,
        decoder: BiFunction<Protocol, BinaryReader, P>,
    ) {
        decode(type) { protocol, reader -> decoder.apply(protocol, reader) }
    }

    fun removeDecoder(type: PacketType) {
        require(type.isInbound) { "Packet '${type.name}' is not an inbound packet" }
        decoderOverrides.remove(type)
    }

    fun <P : OutboundPacket> encode(type: KClass<P>, encoder: PacketEncoder<P>) {
        encoderOverrides[type] = encoder
    }

    fun <P : OutboundPacket> encode(packetClass: Class<P>, encoder: PacketEncoder<P>) {
        encoderOverrides[packetClass.kotlin] = encoder
    }

    fun <P : OutboundPacket> encode(
        packetClass: Class<P>,
        type: PacketType,
        write: BiConsumer<BinaryWriter, P>,
    ) {
        require(type.isOutbound) { "Packet '${type.name}' is not an outbound packet" }
        encode(packetClass) { _, packet ->
            val writer = BinaryWriter()
            write.accept(writer, packet)
            listOf(EncodedPacket(type, writer.toByteArray()))
        }
    }

    inline fun <reified P : OutboundPacket> encode(encoder: PacketEncoder<P>) {
        encode(P::class, encoder)
    }

    inline fun <reified P : OutboundPacket> encode(
        type: PacketType,
        crossinline write: BinaryWriter.(P) -> Unit,
    ) {
        require(type.isOutbound) { "Packet '${type.name}' is not an outbound packet" }
        encode(
            PacketEncoder<P> { _, packet ->
                val writer = BinaryWriter()
                writer.write(packet)
                listOf(EncodedPacket(type, writer.toByteArray()))
            }
        )
    }

    inline fun <reified P : OutboundPacket> encodeEmpty(type: PacketType) {
        require(type.isOutbound) { "Packet '${type.name}' is not an outbound packet" }
        encode(PacketEncoder<P> { _, _ -> listOf(EncodedPacket(type, ByteArray(0))) })
    }

    fun <P : OutboundPacket> encodeEmpty(packetClass: Class<P>, type: PacketType) {
        require(type.isOutbound) { "Packet '${type.name}' is not an outbound packet" }
        encode(packetClass) { _, _ -> listOf(EncodedPacket(type, ByteArray(0))) }
    }

    fun build(): Protocol =
        Protocol(
            version = version,
            protocolVersion = protocolVersion,
            format = format,
            idMapping = idMapping,
            slotSize = slotSize,
            formatChatLinks = formatChatLinks,
            disableCompression = disableCompression,
            autojoinChannels = autojoinChannels,
            decoders = decoderOverrides.toMap(),
            encoders = encoderOverrides.toMap(),
        )
}
