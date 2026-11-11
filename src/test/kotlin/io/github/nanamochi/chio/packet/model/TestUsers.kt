package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Mode
import io.github.nanamochi.chio.packet.types.Mods
import io.github.nanamochi.chio.packet.types.Permissions
import io.github.nanamochi.chio.packet.types.Status

private fun testPresence(isIrc: Boolean = false): UserPresence =
    UserPresence(
        isIrc = isIrc,
        timezone = -5,
        countryIndex = 10,
        permissions = Permissions.Regular,
        longitude = 0.0,
        latitude = 0.0,
        city = "",
    )

fun testStatus(
    action: Status = Status.Idle,
    text: String = "Idle",
    updateStats: Boolean = false,
): UserStatus =
    UserStatus(
        action = action,
        text = text,
        mods = Mods.NoMod,
        mode = Mode.Osu,
        beatmapChecksum = "",
        beatmapId = 0,
        updateStats = updateStats,
    )

fun testStats(rank: Int = 1, accuracy: Float = 100.0f, playcount: Int = 1): UserStats =
    UserStats(
        rank = rank,
        rankedScore = 0,
        totalScore = 0,
        accuracy = accuracy,
        playcount = playcount,
        performancePoints = 100,
    )

fun testUserInfo(
    id: Int = 3,
    name: String = "test_username",
    isIrc: Boolean = false,
    status: UserStatus = testStatus(),
    stats: UserStats = testStats(),
): UserInfo =
    UserInfo(id = id, name = name, presence = testPresence(isIrc), status = status, stats = stats)
