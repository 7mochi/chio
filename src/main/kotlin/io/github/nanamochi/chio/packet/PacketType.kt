package io.github.nanamochi.chio.packet

/**
 * Packet ids shared by every supported client version.
 *
 * A per-version mapping turns them into the raw ids each client understands. The `Osu` prefix marks
 * inbound packets and `Bancho` marks outbound packets.
 */
enum class PacketType(val id: Int) {
    OsuUserStatus(0),
    OsuMessage(1),
    OsuExit(2),
    OsuStatusUpdateRequest(3),
    OsuPong(4),
    BanchoLoginReply(5),
    BanchoCommandError(6),
    BanchoMessage(7),
    BanchoPing(8),
    BanchoIrcChangeUsername(9),
    BanchoIrcQuit(10),
    BanchoUserStats(11),
    BanchoUserQuit(12),
    BanchoSpectatorJoined(13),
    BanchoSpectatorLeft(14),
    BanchoSpectateFrames(15),
    OsuStartSpectating(16),
    OsuStopSpectating(17),
    OsuSpectateFrames(18),
    BanchoVersionUpdate(19),
    OsuErrorReport(20),
    OsuCantSpectate(21),
    BanchoSpectatorCantSpectate(22),
    BanchoGetAttention(23),
    BanchoAnnounce(24),
    OsuPrivateMessage(25),
    BanchoMatchUpdate(26),
    BanchoMatchNew(27),
    BanchoMatchDisband(28),
    OsuLobbyPart(29),
    OsuLobbyJoin(30),
    OsuMatchCreate(31),
    OsuMatchJoin(32),
    OsuMatchPart(33),
    BanchoLobbyJoin(34),
    BanchoLobbyPart(35),
    BanchoMatchJoinSuccess(36),
    BanchoMatchJoinFail(37),
    OsuMatchChangeSlot(38),
    OsuMatchReady(39),
    OsuMatchLock(40),
    OsuMatchChangeSettings(41),
    BanchoFellowSpectatorJoined(42),
    BanchoFellowSpectatorLeft(43),
    OsuMatchStart(44),
    BanchoMatchStart(46),
    OsuMatchScoreUpdate(47),
    BanchoMatchScoreUpdate(48),
    OsuMatchComplete(49),
    BanchoMatchTransferHost(50),
    OsuMatchChangeMods(51),
    OsuMatchLoadComplete(52),
    BanchoMatchAllPlayersLoaded(53),
    OsuMatchNoBeatmap(54),
    OsuMatchNotReady(55),
    OsuMatchFailed(56),
    BanchoMatchPlayerFailed(57),
    BanchoMatchComplete(58),
    OsuMatchHasBeatmap(59),
    OsuMatchSkipRequest(60),
    BanchoMatchSkip(61),
    BanchoUnauthorized(62),
    OsuChannelJoin(63),
    BanchoChannelJoinSuccess(64),
    BanchoChannelAvailable(65),
    BanchoChannelRevoked(66),
    BanchoChannelAvailableAutojoin(67),
    OsuBeatmapInfoRequest(68),
    BanchoBeatmapInfoReply(69),
    OsuMatchTransferHost(70),
    BanchoLoginPermissions(71),
    BanchoFriendsList(72),
    OsuFriendsAdd(73),
    OsuFriendsRemove(74),
    BanchoProtocolNegotiation(75),
    BanchoTitleUpdate(76),
    OsuMatchChangeTeam(77),
    OsuChannelLeave(78),
    OsuReceiveUpdates(79),
    BanchoMonitor(80),
    BanchoMatchPlayerSkipped(81),
    OsuSetIrcAwayMessage(82),
    BanchoUserPresence(83),
    OsuUserStatsRequest(85),
    BanchoRestart(86),
    OsuInvite(87),
    BanchoInvite(88),
    BanchoChannelInfoComplete(89),
    OsuMatchChangePassword(90),
    BanchoMatchChangePassword(91),
    BanchoSilenceInfo(92),
    OsuTournamentMatchInfo(93),
    BanchoUserSilenced(94),
    BanchoUserPresenceSingle(95),
    BanchoUserPresenceBundle(96),
    OsuPresenceRequest(97),
    OsuPresenceRequestAll(98),
    OsuChangeFriendOnlyDms(99),
    BanchoUserDmsBlocked(100),
    BanchoTargetIsSilenced(101),
    BanchoVersionUpdateForced(102),
    BanchoSwitchServer(103),
    BanchoAccountRestricted(104),
    BanchoRTX(105),
    BanchoMatchAbort(106),
    BanchoSwitchTournamentServer(107),
    OsuTournamentJoinMatchChannel(108),
    OsuTournamentLeaveMatchChannel(109),

    /** Inbound packet from older clients without channel support. */
    OsuMatchChangeBeatmap(0xFFFE),

    /** Outbound packet for older clients without channel support. */
    BanchoIrcJoin(0xFFFF);

    val isOutbound: Boolean
        get() = name.startsWith("Bancho")

    val isInbound: Boolean
        get() = name.startsWith("Osu")

    val maxSize: Int
        get() = if (this == OsuBeatmapInfoRequest) 1 shl 18 else 1 shl 14

    companion object {
        private val byId: Map<Int, PacketType> = entries.associateBy(PacketType::id)

        @JvmStatic
        fun from(id: Int): PacketType =
            byId[id] ?: throw IllegalArgumentException("Unknown PacketType id: $id")
    }
}
