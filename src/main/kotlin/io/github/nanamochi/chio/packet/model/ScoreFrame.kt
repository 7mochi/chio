package io.github.nanamochi.chio.packet.model

import java.security.MessageDigest

data class ScoreFrame
@JvmOverloads
constructor(
    val time: Int,
    val id: Int,
    val total300: Int,
    val total100: Int,
    val total50: Int,
    val totalGeki: Int,
    val totalKatu: Int,
    val totalMiss: Int,
    val totalScore: Int,
    val maxCombo: Int,
    val currentCombo: Int,
    val perfect: Boolean,
    val hp: Int,
    /** Protocol-specific tag byte used by newer clients. */
    val tagByte: Int = 0,
    val usingScoreV2: Boolean = false,
    val comboPortion: Double = 0.0,
    val bonusPortion: Double = 0.0,
) {
    val passed: Boolean
        get() = hp != 254

    val checksum: String
        get() {
            val data =
                "$time${if (passed) "True" else "False"}$total300$total50$totalGeki" +
                    "$totalKatu$totalMiss$currentCombo$maxCombo$hp"
            val digest = MessageDigest.getInstance("MD5").digest(data.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
}
