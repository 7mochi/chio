package io.github.nanamochi.chio.packet.outbound

import io.github.nanamochi.chio.packet.model.BeatmapInfo
import io.github.nanamochi.chio.packet.model.Channel
import io.github.nanamochi.chio.packet.model.Match
import io.github.nanamochi.chio.packet.model.Message
import io.github.nanamochi.chio.packet.model.ReplayFrameBundle
import io.github.nanamochi.chio.packet.model.ScoreFrame
import io.github.nanamochi.chio.packet.model.UserInfo
import io.github.nanamochi.chio.packet.model.UserQuit

/** Messages a server sends to an osu! client. */
sealed interface OutboundPacket

data class LoginReplyPacket(val reply: Int) : OutboundPacket

data object PingPacket : OutboundPacket

data class MessagePacket(val message: Message) : OutboundPacket

data class IrcChangeUsernamePacket(val oldName: String, val newName: String) : OutboundPacket

data class UserStatsPacket(val info: UserInfo) : OutboundPacket

data class UserPresencePacket(val info: UserInfo) : OutboundPacket

data class UserPresenceSinglePacket(val info: UserInfo) : OutboundPacket

data class UserPresenceBundlePacket(val infos: List<UserInfo>) : OutboundPacket

data class IrcJoinPacket(val name: String) : OutboundPacket

data class IrcQuitPacket(val name: String) : OutboundPacket

data class SpectatorJoinedPacket(val userId: Int) : OutboundPacket

data class SpectatorLeftPacket(val userId: Int) : OutboundPacket

data class SpectateFramesPacket(val bundle: ReplayFrameBundle) : OutboundPacket

data object VersionUpdatePacket : OutboundPacket

data class SpectatorCantSpectatePacket(val userId: Int) : OutboundPacket

data class UserQuitPacket(val quit: UserQuit) : OutboundPacket

data class InvitePacket(val message: Message) : OutboundPacket

data object GetAttentionPacket : OutboundPacket

data class AnnouncePacket(val message: String) : OutboundPacket

data class RestartPacket @JvmOverloads constructor(val retryAfterMs: Int = 5000) : OutboundPacket

data class MatchUpdatePacket(val match: Match) : OutboundPacket

data class MatchNewPacket(val match: Match) : OutboundPacket

data class MatchDisbandPacket(val matchId: Int) : OutboundPacket

data class LobbyJoinPacket(val userId: Int) : OutboundPacket

data class LobbyPartPacket(val userId: Int) : OutboundPacket

data class MatchJoinSuccessPacket(val match: Match) : OutboundPacket

data object MatchJoinFailPacket : OutboundPacket

data class FellowSpectatorJoinedPacket(val userId: Int) : OutboundPacket

data class FellowSpectatorLeftPacket(val userId: Int) : OutboundPacket

data class MatchStartPacket(val match: Match) : OutboundPacket

data class MatchScoreUpdatePacket(val frame: ScoreFrame) : OutboundPacket

data object MatchTransferHostPacket : OutboundPacket

data object MatchAllPlayersLoadedPacket : OutboundPacket

data class MatchPlayerFailedPacket(val userId: Int) : OutboundPacket

data object MatchCompletePacket : OutboundPacket

data object MatchSkipPacket : OutboundPacket

data class ChannelJoinSuccessPacket(val channel: String) : OutboundPacket

data class ChannelAvailablePacket(val channel: Channel) : OutboundPacket

data class ChannelRevokedPacket(val channel: String) : OutboundPacket

data class ChannelAvailableAutojoinPacket(val channel: Channel) : OutboundPacket

data class BeatmapInfoReplyPacket(val beatmaps: List<BeatmapInfo>) : OutboundPacket
