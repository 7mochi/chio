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
import io.github.nanamochi.chio.packet.types.Completeness
import io.github.nanamochi.chio.packet.types.QuitState
import io.github.nanamochi.chio.protocol.EncodedPacket
import io.github.nanamochi.chio.protocol.Protocol

/** Clients from b365 onward crash when a user's total score exceeds this value. */
private const val MAX_TOTAL_SCORE = 17_705_429_348L

internal fun UserInfo.cappedTotalScore(): UserInfo =
    if (stats.totalScore <= MAX_TOTAL_SCORE) {
        this
    } else {
        copy(stats = stats.copy(totalScore = MAX_TOTAL_SCORE))
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
    writeUShort(info.stats.rank)
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
): List<EncodedPacket> {
    val info = packet.info
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserStatsBodyV338(
        info,
        Completeness.Statistics,
        includeStatusMode,
        includeStatusBeatmapId,
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

fun encodeUserStatsV490(protocol: Protocol, packet: UserStatsPacket): List<EncodedPacket> =
    encodeUserStatsV338Layout(
        protocol,
        packet.copy(info = packet.info.cappedTotalScore()),
        includeStatusMode = true,
        includeStatusBeatmapId = true,
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
) {
    writeUserStatsBodyV338(
        info,
        Completeness.Full,
        includeStatusMode,
        includeStatusBeatmapId,
    )
    writeString(info.name)
    writeString(info.avatarFilename)
    writeUByte(info.presence.timezone + 24)
    writeString(info.presence.countryString)
    if (includePermissions) writeUByte(info.presence.permissions.bits)
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

fun encodeUserPresenceV490(protocol: Protocol, packet: UserPresencePacket): List<EncodedPacket> {
    val info = packet.info.cappedTotalScore()
    if (info.presence.isIrc) return protocol.encodedIrcJoin(info.name)

    val writer = BinaryWriter()
    writer.writeUserPresenceV338Layout(
        info,
        includePermissions = true,
        includeStatusMode = true,
        includeStatusBeatmapId = true,
    )

    return listOf(EncodedPacket(PacketType.BanchoUserStats, writer.toByteArray()))
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
