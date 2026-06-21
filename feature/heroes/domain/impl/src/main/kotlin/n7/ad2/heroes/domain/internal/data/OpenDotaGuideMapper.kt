package n7.ad2.heroes.domain.internal.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroStat
import n7.ad2.heroes.domain.internal.data.model.OpenDotaItemPopularity
import n7.ad2.heroes.domain.internal.data.model.OpenDotaMatchup
import n7.ad2.heroes.domain.model.Guide
import n7.ad2.heroes.domain.model.HeroItem
import n7.ad2.heroes.domain.model.HeroWithWinrate
import n7.ad2.heroes.domain.model.Spell
import kotlin.math.roundToInt

/**
 * Pure mapping from raw OpenDota responses to the domain [Guide]. No IO here so it can be unit-tested.
 *
 * Item / ability artwork is served from the public Valve CDN; counter avatars reuse the bundled hero
 * assets ([avatarUrl]) so they render offline.
 */
internal object OpenDotaGuideMapper {

    private const val ITEM_CDN = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/items"
    private const val ABILITY_CDN = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/abilities"

    private const val MIN_MATCHUP_GAMES = 100
    private const val COUNTERS_COUNT = 8
    private const val START_ITEMS_COUNT = 8
    private const val EARLY_ITEMS_COUNT = 4
    private const val PHASE_ITEMS_COUNT = 8
    private const val SPELLS_COUNT = 6

    /** Bundled hero assets are keyed by the lower-cased display name, e.g. "Anti-Mage" -> "anti-mage". */
    fun assetKey(localizedName: String): String = localizedName.lowercase().replace(" ", "_")

    fun matches(stat: OpenDotaHeroStat, heroName: String): Boolean {
        val target = normalize(heroName)
        return normalize(stat.localizedName) == target ||
            normalize(stat.name.removePrefix("npc_dota_hero_")) == target
    }

    private fun normalize(value: String): String = value.lowercase().filter(Char::isLetterOrDigit)

    /**
     * Extracts a single hero's ability ids from the raw `constants/hero_abilities` body. Lenient on
     * purpose: only string entries are kept, so quirks like Monkey King nesting an array inside its
     * `abilities` list don't break extraction — a strict `Map<String, List<String>>` decode of the
     * whole response throws on that one entry and would otherwise wipe spells for *every* hero.
     */
    fun parseAbilities(json: Json, heroAbilitiesBody: String, heroInternalName: String): List<String> =
        json.parseToJsonElement(heroAbilitiesBody).jsonObject[heroInternalName]
            ?.jsonObject?.get("abilities")?.jsonArray
            ?.mapNotNull { (it as? JsonPrimitive)?.takeIf(JsonPrimitive::isString)?.content }
            .orEmpty()

    fun toGuide(
        heroName: String,
        stats: List<OpenDotaHeroStat>,
        matchups: List<OpenDotaMatchup>,
        itemPopularity: OpenDotaItemPopularity?,
        itemIds: Map<String, String>,
        abilities: List<String>,
    ): Guide? {
        val self = stats.firstOrNull { matches(it, heroName) } ?: return null
        val idToName = stats.associate { it.id to it.localizedName }

        val counters = matchups
            .filter { it.gamesPlayed >= MIN_MATCHUP_GAMES && idToName.containsKey(it.heroId) }
            .map { matchup ->
                val name = idToName.getValue(matchup.heroId)
                HeroWithWinrate(name, round1(percent(matchup.wins.toLong(), matchup.gamesPlayed.toLong())), avatarUrl(name))
            }
        // High win-rate against an enemy => that enemy is easy to win against; low => hard.
        val easyToWin = counters.sortedByDescending { it.heroWinrate }.take(COUNTERS_COUNT)
        val hardToWin = counters.sortedBy { it.heroWinrate }.take(COUNTERS_COUNT)

        val startingItems = topItems(itemPopularity?.startGameItems, itemIds, START_ITEMS_COUNT)
            .map { HeroItem(it, "") }
        val mainItems = buildList {
            topItems(itemPopularity?.earlyGameItems, itemIds, EARLY_ITEMS_COUNT).forEach { add(HeroItem(it, "Early")) }
            topItems(itemPopularity?.midGameItems, itemIds, PHASE_ITEMS_COUNT).forEach { add(HeroItem(it, "Mid")) }
            topItems(itemPopularity?.lateGameItems, itemIds, PHASE_ITEMS_COUNT).forEach { add(HeroItem(it, "Late")) }
        }

        val spells = abilities
            .filterNot { it == "generic_hidden" || it.startsWith("special_bonus") }
            .take(SPELLS_COUNT)
            .mapIndexed { index, ability -> Spell(prettify(ability), "${index + 1}", "$ABILITY_CDN/$ability.png") }

        return Guide(
            heroName = self.localizedName,
            heroWinrate = "${round1(percent(self.totalWin, self.totalPick))}%",
            heroPopularity = formatPicks(self.totalPick),
            guideTime = "",
            hardToWinHeroList = hardToWin,
            easyToWinHeroList = easyToWin,
            heroStartingHeroItemsList = startingItems,
            heroItemsList = mainItems,
            heroSpellsList = spells,
        )
    }

    private fun topItems(map: Map<String, Int>?, itemIds: Map<String, String>, count: Int): List<String> =
        (map ?: emptyMap()).entries
            .sortedByDescending { it.value }
            .mapNotNull { itemIds[it.key] }
            .filter { it.isNotBlank() }
            .distinct()
            .take(count)

    private fun avatarUrl(localizedName: String) = "file:///android_asset/heroes/${assetKey(localizedName)}/full.webp"

    private fun percent(win: Long, total: Long): Double = if (total > 0) win * 100.0 / total else 0.0

    private fun round1(value: Double): Double = (value * 10).roundToInt() / 10.0

    private fun prettify(internalName: String): String =
        internalName.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    private fun formatPicks(total: Long): String = when {
        total >= 1_000_000 -> "${round1(total / 1_000_000.0)}M"
        total >= 1_000 -> "${round1(total / 1_000.0)}K"
        else -> total.toString()
    }
}
