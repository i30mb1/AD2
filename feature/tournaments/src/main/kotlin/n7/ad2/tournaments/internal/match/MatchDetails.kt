package n7.ad2.tournaments.internal.match

import n7.ad2.tournaments.internal.data.MatchStatus

/** "Left" / "right" are the two teams in the order the site lists them (not the in-game radiant/dire side, which swaps between games). */
internal data class MatchDetails(
    val url: String,
    val tournament: String,
    val tournamentLogoUrl: String,
    val prize: String?,
    val stage: String?,
    val startSeconds: Long,
    val bestOf: Int,
    val status: MatchStatus,
    val left: DetailsTeam,
    val right: DetailsTeam,
    val scoreLeft: Int,
    val scoreRight: Int,
    val maps: List<GameMap>,
    val headToHead: List<PastMatch>,
    val forecast: Forecast?,
    /** Twitch channel when the match is streamed there, null otherwise. */
    val twitchChannel: String?,
    /** Any stream link (used when there is no Twitch channel). */
    val streamUrl: String?,
)

internal data class DetailsTeam(
    val name: String,
    val logoUrl: String,
    val rating: Int?,
    val position: Int?,
    val players: List<DetailsPlayer>,
)

internal data class DetailsPlayer(
    val nick: String,
    val flagUrl: String?,
    val leaderboard: Int?,
    val isCaptain: Boolean,
    val kda: Double?,
)

internal data class GameMap(
    val number: Int,
    val isFinished: Boolean,
    /** Null while the game is not finished. */
    val winnerIsLeft: Boolean?,
    val durationSeconds: Int,
    val killsLeft: Int,
    val killsRight: Int,
    val draft: List<DraftHero>,
    val players: List<MapPlayer>,
)

internal data class DraftHero(val name: String, val iconUrl: String, val isPick: Boolean, val isLeft: Boolean)

internal data class MapPlayer(
    val nick: String,
    val heroName: String,
    val heroIconUrl: String,
    val level: Int,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val gpm: Int,
    val xpm: Int,
    val netWorth: Int,
    val itemIconUrls: List<String>,
    val isLeft: Boolean,
)

internal data class PastMatch(
    val startSeconds: Long,
    val tournament: String,
    val leftName: String,
    val leftLogoUrl: String,
    val rightName: String,
    val rightLogoUrl: String,
    val scoreLeft: Int,
    val scoreRight: Int,
)

internal data class Forecast(val leftPercent: Int, val drawPercent: Int, val rightPercent: Int)
