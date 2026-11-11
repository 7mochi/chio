package io.github.nanamochi.chio

import io.github.nanamochi.chio.packet.PacketType

open class ChioException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

class MalformedPacketException(message: String, cause: Throwable? = null) :
    ChioException(message, cause)

/** Thrown when a packet exceeds its size limit, before or after decompression. */
class PacketTooLargeException(length: Long, maxSize: Int) :
    ChioException("Packet length $length is too large (max $maxSize)")

class UnsupportedPacketException(
    packetType: PacketType?,
    val version: Int,
    direction: String,
) :
    ChioException(
        "Unsupported $direction packet ${packetType ?: "<unknown>"} for client version $version"
    )
