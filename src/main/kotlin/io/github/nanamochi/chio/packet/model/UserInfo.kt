package io.github.nanamochi.chio.packet.model

data class UserInfo
@JvmOverloads
constructor(
    val id: Int = 0,
    val name: String = "",
    val presence: UserPresence = UserPresence(),
    val status: UserStatus = UserStatus(),
    val stats: UserStats = UserStats(),
) {
    val avatarFilename: String
        get() = "${id}_000.png"
}
