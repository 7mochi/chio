package io.github.nanamochi.chio

/**
 * Default limit for a packet read without a [io.github.nanamochi.chio.packet.PacketType] context.
 *
 * Normal decoding passes [io.github.nanamochi.chio.packet.PacketType.maxSize]. This limit protects
 * direct [io.github.nanamochi.chio.protocol.PacketFormat] callers from reading a packet of
 * unbounded size.
 */
const val DEFAULT_MAX_PACKET_SIZE: Int = 16 * 1024 * 1024
