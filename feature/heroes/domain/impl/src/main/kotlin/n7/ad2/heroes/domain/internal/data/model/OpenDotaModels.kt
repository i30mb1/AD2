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
) {
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

/** Value type for https://api.opendota.com/api/constants/hero_abilities (keyed by npc_dota_hero_*). */
@Serializable
internal class OpenDotaHeroAbilities(
    val abilities: List<String> = emptyList(),
)
