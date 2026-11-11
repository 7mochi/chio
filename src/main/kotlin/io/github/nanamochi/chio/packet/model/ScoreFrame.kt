package io.github.nanamochi.chio.packet.model

import java.security.MessageDigest

data class ScoreFrame(
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
