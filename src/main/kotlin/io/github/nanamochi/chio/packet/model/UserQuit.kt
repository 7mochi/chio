package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.QuitState

data class UserQuit
@JvmOverloads
constructor(val info: UserInfo = UserInfo(), val state: QuitState = QuitState.Gone)
