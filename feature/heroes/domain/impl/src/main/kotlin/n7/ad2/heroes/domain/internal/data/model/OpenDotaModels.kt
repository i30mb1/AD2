package n7.ad2.heroes.domain.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Subset of https://api.opendota.com/api/heroStats — one entry per hero.
 * The `<bracket>_pick` / `<bracket>_win` fields are public-match counts for skill brackets 1..8.
 */
@Serializable
internal class OpenDotaHeroStat(
    val id: Int = 0,
    val name: String = "",
    @SerialName("localized_name") val localizedName: String = "",
    @SerialName("1_pick") val pick1: Long = 0,
    @SerialName("1_win") val win1: Long = 0,
    @SerialName("2_pick") val pick2: Long = 0,
    @SerialName("2_win") val win2: Long = 0,
    @SerialName("3_pick") val pick3: Long = 0,
    @SerialName("3_win") val win3: Long = 0,
    @SerialName("4_pick") val pick4: Long = 0,
    @SerialName("4_win") val win4: Long = 0,
    @SerialName("5_pick") val pick5: Long = 0,
    @SerialName("5_win") val win5: Long = 0,
    @SerialName("6_pick") val pick6: Long = 0,
    @SerialName("6_win") val win6: Long = 0,
    @SerialName("7_pick") val pick7: Long = 0,
    @SerialName("7_win") val win7: Long = 0,
    @SerialName("8_pick") val pick8: Long = 0,
    @SerialName("8_win") val win8: Long = 0,
    @SerialName("pro_pick") val proPick: Int = 0,
    @SerialName("pro_win") val proWin: Int = 0,
    @SerialName("pro_ban") val proBan: Int = 0,
) {
    /** Picks and wins per skill bracket, index 0 is bracket 1. */
    val brackets: List<Pair<Long, Long>>
        get() = listOf(pick1 to win1, pick2 to win2, pick3 to win3, pick4 to win4, pick5 to win5, pick6 to win6, pick7 to win7, pick8 to win8)

    val totalPick: Long get() = pick1 + pick2 + pick3 + pick4 + pick5 + pick6 + pick7 + pick8
    val totalWin: Long get() = win1 + win2 + win3 + win4 + win5 + win6 + win7 + win8
}

/** https://api.opendota.com/api/heroes/{id}/matchups — the queried hero vs every other hero. */
@Serializable
internal class OpenDotaMatchup(
    @SerialName("hero_id") val heroId: Int = 0,
    @SerialName("games_played") val gamesPlayed: Int = 0,
    val wins: Int = 0,
)

/** https://api.opendota.com/api/heroes/{id}/itemPopularity — maps are item-id -> usage count. */
@Serializable
internal class OpenDotaItemPopularity(
    @SerialName("start_game_items") val startGameItems: Map<String, Int> = emptyMap(),
    @SerialName("early_game_items") val earlyGameItems: Map<String, Int> = emptyMap(),
    @SerialName("mid_game_items") val midGameItems: Map<String, Int> = emptyMap(),
    @SerialName("late_game_items") val lateGameItems: Map<String, Int> = emptyMap(),
)

/** https://api.opendota.com/api/heroes/{id}/durations — [durationBin] is the match length in seconds, in 5 minute steps. */
@Serializable
internal class OpenDotaDuration(
    @SerialName("duration_bin") val durationBin: Int = 0,
    @SerialName("games_played") val gamesPlayed: Int = 0,
    val wins: Int = 0,
)

/** https://api.opendota.com/api/heroes/{id}/matches — recent pro matches with this hero. */
@Serializable
internal class OpenDotaHeroMatch(
    @SerialName("match_id") val matchId: Long = 0,
)

/** https://api.opendota.com/api/matches/{id}, only what the skill build needs. */
@Serializable
internal class OpenDotaMatch(
    val players: List<OpenDotaMatchPlayer> = emptyList(),
)

/** [abilityUpgrades] are ability ids in the order the points were spent, null when the replay is not parsed. */
@Serializable
internal class OpenDotaMatchPlayer(
    @SerialName("hero_id") val heroId: Int = 0,
    @SerialName("ability_upgrades_arr") val abilityUpgrades: List<Int>? = null,
)
