package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.PacketType
import io.github.nanamochi.chio.packet.model.UserInfo
import io.github.nanamochi.chio.packet.outbound.IrcJoinPacket
import io.github.nanamochi.chio.packet.outbound.IrcQuitPacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceBundlePacket
import io.github.nanamochi.chio.packet.outbound.UserPresencePacket
import io.github.nanamochi.chio.packet.outbound.UserPresenceSinglePacket
import io.github.nanamochi.chio.packet.outbound.UserQuitPacket
import io.github.nanamochi.chio.packet.outbound.UserStatsPacket
import io.github.nanamochi.chio.packet.types.AvatarExtension
import io.github.nanamochi.chio.packet.types.Completeness
import io.github.nanamochi.chio.packet.types.QuitState
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.Protocol
import kotlin.math.abs

/** b365 to b490 clients crash when a user's total score exceeds this value. */
private const val MAX_TOTAL_SCORE_V365 = 17_705_429_348L

/** b504 and later clients crash when a user's total score exceeds this higher value. */
private const val MAX_TOTAL_SCORE_V504 = 26_931_190_827L

internal fun UserInfo.cappedTotalScore(maxTotalScore: Long? = MAX_TOTAL_SCORE_V365): UserInfo {
    val cap = maxTotalScore ?: return this
    return if (stats.totalScore <= cap) {
        this
    } else {
        copy(stats = stats.copy(totalScore = cap))
    }
}

private fun Protocol.encodedIrcJoin(name: String): List<EncodedPacket> =
    encodedPackets(IrcJoinPacket(name)).orEmpty()

private fun BinaryWriter.writeUserStatsBodyV282(info: UserInfo) {
    writeUInt(info.id)
    writeString(info.name)
    writeULong(info.stats.rankedScore)
    writeDouble(info.stats.accuracy.toDouble())
    writeUInt(info.stats.playcount)
    writeULong(info.stats.totalScore)
    writeUInt(info.stats.rank)
    writeString(info.avatarFilename)
    writeStatusV282(info.status)
    writeUByte(info.presence.timezone + 24)
    writeString(info.presence.countryString)
}

private fun BinaryWriter.writeUserStatsBodyV338(
    info: UserInfo,
    completeness: Completeness,
    includeStatusMode: Boolean = false,
    includeStatusBeatmapId: Boolean = false,
    rankAsS32: Boolean = false,
) {
    writeUInt(info.id)
    writeUByte(completeness.value)
    when {
        includeStatusBeatmapId -> writeStatusV490(info.status)
        includeStatusMode -> writeStatusV489(info.status)
        else -> writeStatusV338(info.status)
    }
    writeULong(info.stats.rankedScore)
    writeFloat(info.stats.accuracy)
    writeUInt(info.stats.playcount)
    writeULong(info.stats.totalScore)
    if (rankAsS32) writeInt(info.stats.rank) else writeUShort(info.stats.rank)
}

fun encodeUserStatsV282(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserStatsBodyV282(info)
    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserStatsV323(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUInt(info.id)
    writer.writeBoolean(info.status.updateStats)

    if (info.status.updateStats) {
        writer.writeString(info.name)
        writer.writeULong(info.stats.rankedScore)
        writer.writeFloat(info.stats.accuracy)
        writer.writeUInt(info.stats.playcount)
        writer.writeULong(info.stats.totalScore)
        writer.writeUInt(info.stats.rank)
        writer.writeString(info.avatarFilename)
        writer.writeUByte(info.presence.timezone + 24)
        writer.writeString(info.presence.countryString)
    }

    writer.writeStatusV282(info.status)
    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

private fun encodeUserStatsV338Layout(
    protocol: Protocol,
    packet: UserStatsPacket,
    includeStatusMode: Boolean,
    includeStatusBeatmapId: Boolean = false,
    rankAsS32: Boolean = false,
): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserStatsBodyV338(
        info,
        Completeness.Statistics,
        includeStatusMode = includeStatusMode,
        includeStatusBeatmapId = includeStatusBeatmapId,
        rankAsS32 = rankAsS32,
    )

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserStatsV338(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV338Layout(protocol, packet, includeStatusMode = false)

fun encodeUserStatsV489(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV338Layout(
        protocol,
        packet.copy(info = packet.info.cappedTotalScore()),
        includeStatusMode = true,
    )

private fun encodeUserStatsV490Layout(
    protocol: Protocol,
    packet: UserStatsPacket,
    maxTotalScore: Long?,
    rankAsS32: Boolean = false,
): List<EncodedPacket> =
    encodeUserStatsV338Layout(
        protocol,
        packet.copy(info = packet.info.cappedTotalScore(maxTotalScore)),
        includeStatusMode = true,
        includeStatusBeatmapId = true,
        rankAsS32 = rankAsS32,
    )

fun encodeUserStatsV490(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV490Layout(protocol, packet, MAX_TOTAL_SCORE_V365)

fun encodeUserStatsV504(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV490Layout(protocol, packet, MAX_TOTAL_SCORE_V504)

fun encodeUserStatsV659(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV490Layout(protocol, packet, maxTotalScore = null)

fun encodeUserStatsV695(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV490Layout(
        protocol,
        packet,
        maxTotalScore = null,
        rankAsS32 = true,
    )

fun encodeUserPresenceV282(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> =
    protocol.encodedPackets(UserStatsPacket(packet.info)).orEmpty()

fun encodeUserPresenceV323(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val withStats = UserStatsPacket(info.copy(status = info.status.copy(updateStats = true)))
    val withoutStats = UserStatsPacket(info.copy(status = info.status.copy(updateStats = false)))

    return protocol.encodedPackets(withStats).orEmpty() +
        protocol.encodedPackets(withoutStats).orEmpty()
}

private fun BinaryWriter.writeUserPresenceV338Layout(
    info: UserInfo,
    includePermissions: Boolean,
    includeStatusMode: Boolean = false,
    includeStatusBeatmapId: Boolean = false,
    rankAsS32: Boolean = false,
    includeLocation: Boolean = false,
) {
    writeUserStatsBodyV338(
        info,
        Completeness.Full,
        includeStatusMode = includeStatusMode,
        includeStatusBeatmapId = includeStatusBeatmapId,
        rankAsS32 = rankAsS32,
    )
    writeString(info.name)
    writeString(info.avatarFilename)
    writeUByte(info.presence.timezone + 24)
    writeString(info.presence.countryString)
    if (includePermissions) writeUByte(info.presence.permissions.bits)
    if (includeLocation) {
        writeFloat(info.presence.longitude.toFloat())
        writeFloat(info.presence.latitude.toFloat())
    }
}

fun encodeUserPresenceV338(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserPresenceV338Layout(info, includePermissions = false)

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserPresenceV452(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info.cappedTotalScore()
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserPresenceV338Layout(info, includePermissions = true)

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserPresenceV489(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info.cappedTotalScore()
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserPresenceV338Layout(
        info,
        includePermissions = true,
        includeStatusMode = true,
    )

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

private fun encodeUserPresenceV490Layout(
    protocol: Protocol,
    packet: UserPresencePacket,
    maxTotalScore: Long?,
    rankAsS32: Boolean = false,
    includeLocation: Boolean = false,
): List<EncodedPacket> {
    val info = packet.info.cappedTotalScore(maxTotalScore)
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserPresenceV338Layout(
        info,
        includePermissions = true,
        includeStatusMode = true,
        includeStatusBeatmapId = true,
        rankAsS32 = rankAsS32,
        includeLocation = includeLocation,
    )

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserPresenceV490(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV490Layout(protocol, packet, MAX_TOTAL_SCORE_V365)

fun encodeUserPresenceV504(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV490Layout(protocol, packet, MAX_TOTAL_SCORE_V504)

fun encodeUserPresenceV659(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV490Layout(protocol, packet, maxTotalScore = null)

fun encodeUserPresenceV695(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV490Layout(
        protocol,
        packet,
        maxTotalScore = null,
        rankAsS32 = true,
    )

fun encodeUserPresenceV1183(
    protocol: Protocol,
    packet: UserPresencePacket,
): List<EncodedPacket> =
    encodeUserPresenceV490Layout(
        protocol,
        packet,
        maxTotalScore = null,
        rankAsS32 = true,
        includeLocation = true,
    )

fun encodeUserStatsV1788(packet: UserStatsPacket): List<EncodedPacket> {
    val info = packet.info
    val writer = BinaryWriter()
    writer.writeInt(info.id)
    writer.writeStatusV1788(info.status)
    writer.writeULong(info.stats.rankedScore)
    writer.writeFloat(info.stats.accuracy)
    writer.writeUInt(info.stats.playcount)
    writer.writeULong(info.stats.totalScore)
    writer.writeInt(info.stats.rank)

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserStatsV20120723(packet: UserStatsPacket): List<EncodedPacket> {
    val info = packet.info
    val writer = BinaryWriter()
    writer.writeInt(info.id)
    writer.writeStatusV1788(info.status)
    writer.writeULong(info.stats.rankedScore)
    writer.writeFloat(info.stats.accuracy)
    writer.writeUInt(info.stats.playcount)
    writer.writeULong(info.stats.totalScore)
    writer.writeInt(info.stats.rank)
    writer.writeShort(info.stats.performancePoints)

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
}

fun encodeUserPresenceV1788(packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info
    val userId = if (info.presence.isIrc) -abs(info.id) else abs(info.id)
    val writer = BinaryWriter()
    writer.writeInt(userId)
    writer.writeString(info.name)
    writer.writeUByte(AvatarExtension.Png.value)
    writer.writeUByte(info.presence.timezone + 24)
    writer.writeString(info.presence.countryString)
    writer.writeUByte(info.presence.permissions.bits)
    writer.writeFloat(info.presence.longitude.toFloat())
    writer.writeFloat(info.presence.latitude.toFloat())

    return listOf(EncodedPacket(PacketType.BanchoUserPresence, writer.toByteArray()))
}

fun encodeUserPresenceV1796(packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV1796Layout(packet, includeRank = false)

fun encodeUserPresenceV1797(packet: UserPresencePacket): List<EncodedPacket> =
    encodeUserPresenceV1796Layout(packet, includeRank = true)

private fun encodeUserPresenceV1796Layout(
    packet: UserPresencePacket,
    includeRank: Boolean,
): List<EncodedPacket> {
    val info = packet.info
    val userId = if (info.presence.isIrc) -abs(info.id) else abs(info.id)
    val writer = BinaryWriter()
    writer.writeInt(userId)
    writer.writeString(info.name)
    writer.writeUByte(AvatarExtension.Png.value)
    writer.writeUByte(info.presence.timezone + 24)
    writer.writeUByte(info.presence.countryIndex)
    writer.writeString(info.presence.city)
    writer.writeUByte(info.presence.permissions.bits)
    writer.writeFloat(info.presence.longitude.toFloat())
    writer.writeFloat(info.presence.latitude.toFloat())
    if (includeRank) writer.writeInt(info.stats.rank)

    return listOf(EncodedPacket(PacketType.BanchoUserPresence, writer.toByteArray()))
}

fun encodeUserPresenceSingle(
    protocol: Protocol,
    packet: UserPresenceSinglePacket,
): List<EncodedPacket> = protocol.encodedPackets(UserPresencePacket(packet.info)).orEmpty()

fun encodeUserPresenceBundle(
    protocol: Protocol,
    packet: UserPresenceBundlePacket,
): List<EncodedPacket> =
    packet.infos.flatMap { protocol.encodedPackets(UserPresenceSinglePacket(it)).orEmpty() }

fun encodeUserQuit(protocol: Protocol, packet: UserQuitPacket): List<EncodedPacket> {
    val info: UserInfo = packet.quit.info
    val state: QuitState = packet.quit.state

    if (info.presence.isIrc && state != QuitState.IrcRemaining) {
        return protocol.encodedPackets(IrcQuitPacket(info.name)).orEmpty()
    }

    if (state == QuitState.OsuRemaining) return emptyList()

    if (info.presence.isIrc) {
        val writer = BinaryWriter()
        writer.writeString(info.name)
        return listOf(EncodedPacket(PacketType.BanchoUserQuit, writer.toByteArray()))
    }

    val stats = protocol.encodedPackets(UserStatsPacket(info)).orEmpty()

    return stats.take(1).map { EncodedPacket(PacketType.BanchoUserQuit, it.payload) }
}
