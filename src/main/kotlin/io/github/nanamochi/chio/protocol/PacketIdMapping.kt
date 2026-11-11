package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.packet.PacketType

/**
 * Packet ids differ between client builds.
 *
 * This maps the shared ids to the raw ids each build uses.
 */
interface PacketIdMapping {
    fun packetType(rawId: Int): PacketType

    fun rawId(type: PacketType): Int
}

/** Shared by 282, 291, 294, 296, 298, 312 and 320. */
object PacketIdMappingV282 : PacketIdMapping {
    override fun packetType(rawId: Int): PacketType {
        var packet = rawId
        if (packet == 11) return PacketType.BanchoIrcJoin
        if (packet in 12..45) packet--
        if (packet > 50) packet--

        return PacketType.from(packet)
    }

    override fun rawId(type: PacketType): Int {
        if (type == PacketType.BanchoIrcJoin) return 11

        val value = type.id
        if (value in 11..44) return value + 1
        if (value > 50) return value + 1

        return value
    }
}

/** 323 adds a raw id for `MatchChangeBeatmap`. */
object PacketIdMappingV323 : PacketIdMapping {
    override fun packetType(rawId: Int): PacketType {
        var packet = rawId
        if (packet == 11) return PacketType.BanchoIrcJoin
        if (packet == 50) return PacketType.OsuMatchChangeBeatmap
        if (packet in 12..45) packet--

        return PacketType.from(packet)
    }

    override fun rawId(type: PacketType): Int {
        if (type == PacketType.BanchoIrcJoin) return 11
        if (type == PacketType.OsuMatchChangeBeatmap) return 50

        val value = type.id
        if (value in 11..44) return value + 1

        return value
    }
}

/** 334 and 338 add ids 50–58 and use 51 for `MatchChangeBeatmap`. */
object PacketIdMappingV334 : PacketIdMapping {
    override fun packetType(rawId: Int): PacketType {
        var packet = rawId
        if (packet == 11) return PacketType.BanchoIrcJoin
        if (packet == 51) return PacketType.OsuMatchChangeBeatmap
        if (packet > 11) packet--
        if (packet >= 51) packet--

        return PacketType.from(packet)
    }

    override fun rawId(type: PacketType): Int {
        if (type == PacketType.BanchoIrcJoin) return 11
        if (type == PacketType.OsuMatchChangeBeatmap) return 51

        var value = type.id
        if (value >= 11) value++
        if (value >= 51) value++

        return value
    }
}

/** 340 drops `MatchChangeBeatmap`. */
object PacketIdMappingV340 : PacketIdMapping {
    override fun packetType(rawId: Int): PacketType {
        var packet = rawId
        if (packet == 11) return PacketType.BanchoIrcJoin
        if (packet > 11) packet--

        return PacketType.from(packet)
    }

    override fun rawId(type: PacketType): Int {
        if (type == PacketType.BanchoIrcJoin) return 11

        val value = type.id
        if (value >= 11) return value + 1

        return value
    }
}
