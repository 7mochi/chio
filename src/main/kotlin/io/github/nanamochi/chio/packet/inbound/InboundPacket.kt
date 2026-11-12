package io.github.nanamochi.chio.packet.inbound

import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.MatchJoin
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.model.UserStatus
import io.github.nanamochi.chio.packet.types.Mods

/** Messages a server receives from an osu! client. */
sealed interface InboundPacket

data class UserStatusPacket(val status: UserStatus) : InboundPacket

data class MessagePacket(val message: Message) : InboundPacket

data class PrivateMessagePacket(val message: Message) : InboundPacket

data object ExitPacket : InboundPacket

data object StatusUpdateRequestPacket : InboundPacket

data object PongPacket : InboundPacket

data class StartSpectatingPacket(val userId: Int) : InboundPacket

data object StopSpectatingPacket : InboundPacket

data class SpectateFramesPacket(val bundle: ReplayFrameBundle) : InboundPacket

data class ErrorReportPacket(val message: String) : InboundPacket

data object CantSpectatePacket : InboundPacket

data object LobbyJoinPacket : InboundPacket

data object LobbyPartPacket : InboundPacket

data class MatchCreatePacket(val match: Match) : InboundPacket

data class MatchJoinPacket(val join: MatchJoin) : InboundPacket

data object MatchPartPacket : InboundPacket

data class MatchChangeSlotPacket(val slotId: Int) : InboundPacket

data object MatchReadyPacket : InboundPacket

data class MatchLockPacket(val slotId: Int) : InboundPacket

data class MatchChangeSettingsPacket(val match: Match) : InboundPacket

data object MatchStartPacket : InboundPacket

data class MatchScoreUpdatePacket(val frame: ScoreFrame) : InboundPacket

data object MatchCompletePacket : InboundPacket

data class MatchChangeModsPacket(val mods: Mods) : InboundPacket

data object MatchLoadCompletePacket : InboundPacket

data object MatchNoBeatmapPacket : InboundPacket

data object MatchNotReadyPacket : InboundPacket

data object MatchFailedPacket : InboundPacket

data object MatchHasBeatmapPacket : InboundPacket

data object MatchSkipRequestPacket : InboundPacket

data class MatchChangeBeatmapPacket(val match: Match) : InboundPacket
