package n7.ad2.heroes.domain.internal.data

import n7.ad2.heroes.domain.internal.data.model.OpenDotaDuration
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroStat
import n7.ad2.heroes.domain.internal.data.model.OpenDotaItemPopularity
import n7.ad2.heroes.domain.internal.data.model.OpenDotaMatchup
import n7.ad2.heroes.domain.model.DurationWinrate
import n7.ad2.heroes.domain.model.Guide
import n7.ad2.heroes.domain.model.HeroItem
import n7.ad2.heroes.domain.model.HeroWithWinrate
import n7.ad2.heroes.domain.model.RankWinrate
import n7.ad2.heroes.domain.model.SkillUpgrade
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

    /** OpenDota has few games for most matchups: a list short of heroes takes ones with fewer games, never under the last. */
    private val MATCHUP_MIN_GAMES = listOf(100, 50, 30)
    private const val COUNTERS_COUNT = 8
    private const val EVEN_MATCHUP = 50.0
    private const val START_ITEMS_COUNT = 8
    private const val EARLY_ITEMS_COUNT = 4
    private const val PHASE_ITEMS_COUNT = 8
    private const val MIN_DURATION_GAMES = 50
    private const val DURATION_STEP_MINUTES = 10
    private const val SHORTEST_DURATION_GROUP_MINUTES = 20
    private const val LONGEST_DURATION_GROUP_MINUTES = 50
    private const val SKILL_BUILD_LENGTH = 18
    private const val ATTRIBUTES_KEY = "special_bonus_attributes"

    /** Bundled hero assets are keyed by the lower-cased display name, e.g. "Anti-Mage" -> "anti-mage". */
    fun assetKey(localizedName: String): String = localizedName.lowercase().replace(" ", "_")

    fun matches(stat: OpenDotaHeroStat, heroName: String): Boolean {
        val target = normalize(heroName)
        return normalize(stat.localizedName) == target ||
            normalize(stat.name.removePrefix("npc_dota_hero_")) == target
    }

    private fun normalize(value: String): String = value.lowercase().filter(Char::isLetterOrDigit)

    fun toGuide(
        heroName: String,
        stats: List<OpenDotaHeroStat>,
        matchups: List<OpenDotaMatchup>,
        itemPopularity: OpenDotaItemPopularity?,
        itemIds: Map<String, String>,
        durations: List<OpenDotaDuration>,
        upgradeSequences: List<List<String>>,
    ): Guide? {
        val self = stats.firstOrNull { matches(it, heroName) } ?: return null
        val idToName = stats.associate { it.id to it.localizedName }

        val counters = matchups
            .filter { idToName.containsKey(it.heroId) }
            .map { matchup ->
                val name = idToName.getValue(matchup.heroId)
                matchup.gamesPlayed to HeroWithWinrate(name, round1(percent(matchup.wins.toLong(), matchup.gamesPlayed.toLong())), avatarUrl(name))
            }
        // High win-rate against an enemy => that enemy is easy to win against; low => hard.
        // Split at an even matchup so with few matchups the same heroes don't show up in both lists.
        val easyToWin = counters.side { it >= EVEN_MATCHUP }.sortedByDescending { it.heroWinrate }.take(COUNTERS_COUNT)
        val hardToWin = counters.side { it < EVEN_MATCHUP }.sortedBy { it.heroWinrate }.take(COUNTERS_COUNT)

        val startingItems = topItems(itemPopularity?.startGameItems, itemIds, START_ITEMS_COUNT)
            .map { HeroItem(it, "") }
        val mainItems = buildList {
            topItems(itemPopularity?.earlyGameItems, itemIds, EARLY_ITEMS_COUNT).forEach { add(HeroItem(it, "Early")) }
            topItems(itemPopularity?.midGameItems, itemIds, PHASE_ITEMS_COUNT).forEach { add(HeroItem(it, "Mid")) }
            topItems(itemPopularity?.lateGameItems, itemIds, PHASE_ITEMS_COUNT).forEach { add(HeroItem(it, "Late")) }
        }

        val rankWinrates = self.brackets.mapIndexedNotNull { index, (picks, wins) ->
            if (picks > 0) RankWinrate(index + 1, round1(percent(wins, picks))) else null
        }

        return Guide(
            heroName = self.localizedName,
            heroWinrate = "${round1(percent(self.totalWin, self.totalPick))}%",
            heroPopularity = formatPicks(self.totalPick),
            proPicks = self.proPick,
            proWins = self.proWin,
            proBans = self.proBan,
            rankWinrates = rankWinrates,
            durationWinrates = durationWinrates(durations),
            hardToWinHeroList = hardToWin,
            easyToWinHeroList = easyToWin,
            heroStartingHeroItemsList = startingItems,
            heroItemsList = mainItems,
            skillBuild = skillBuild(upgradeSequences),
        )
    }

    /** 10 minute groups, everything shorter than 20 and longer than 50 minutes is merged so the edges have enough games. */
    private fun durationWinrates(durations: List<OpenDotaDuration>): List<DurationWinrate> = durations
        .groupBy { duration ->
            val minutes = duration.durationBin / 60
            (minutes / DURATION_STEP_MINUTES * DURATION_STEP_MINUTES)
                .coerceIn(SHORTEST_DURATION_GROUP_MINUTES - DURATION_STEP_MINUTES, LONGEST_DURATION_GROUP_MINUTES)
        }
        .toSortedMap()
        .mapNotNull { (group, items) ->
            val games = items.sumOf { it.gamesPlayed.toLong() }
            if (games < MIN_DURATION_GAMES) return@mapNotNull null
            val from = if (group < SHORTEST_DURATION_GROUP_MINUTES) 0 else group
            val to = if (group >= LONGEST_DURATION_GROUP_MINUTES) null else group + DURATION_STEP_MINUTES
            DurationWinrate(from, to, round1(percent(items.sumOf { it.wins.toLong() }, games)))
        }

    /** Heroes on one side of the even matchup, with as many games as the side can afford, see [MATCHUP_MIN_GAMES]. */
    private fun List<Pair<Int, HeroWithWinrate>>.side(isOnSide: (winrate: Double) -> Boolean): List<HeroWithWinrate> {
        val onSide = filter { (_, hero) -> isOnSide(hero.heroWinrate) }
        val minGames = MATCHUP_MIN_GAMES.firstOrNull { min -> onSide.count { (games, _) -> games >= min } >= COUNTERS_COUNT }
            ?: MATCHUP_MIN_GAMES.last()
        return onSide.filter { (games, _) -> games >= minGames }.map { (_, hero) -> hero }
    }

    /**
     * Picks one real build out of [sequences] instead of gluing the most popular choice of every level together:
     * a glued build can put five points into a four level ability. The chosen build is the one that agrees with
     * the majority on the most levels, ties go to the earlier (more recent) match. A build that never takes an
     * ability most players take is left out: a pro sometimes skips one for the whole game, it says little about the hero.
     */
    private fun skillBuild(sequences: List<List<String>>): List<SkillUpgrade> {
        val builds = sequences.map { it.take(SKILL_BUILD_LENGTH) }.filter { it.isNotEmpty() }
        if (builds.isEmpty()) return emptyList()
        val length = builds.maxOf { it.size }
        val majority = (0 until length).map { level ->
            builds.mapNotNull { it.getOrNull(level) }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        }
        val usualAbilities = builds
            .flatMap { build -> build.filterNot { it.startsWith("special_bonus") }.distinct() }
            .groupingBy { it }
            .eachCount()
            .filter { (_, count) -> count * 2 > builds.size }
            .keys
        val candidates = builds.filter { it.containsAll(usualAbilities) }.ifEmpty { builds }
        val best = candidates.maxBy { build -> build.indices.count { build[it] == majority[it] } }
        return best.map { key ->
            val kind = when {
                key == ATTRIBUTES_KEY -> SkillUpgrade.Kind.ATTRIBUTES
                key.startsWith("special_bonus") -> SkillUpgrade.Kind.TALENT
                else -> SkillUpgrade.Kind.ABILITY
            }
            SkillUpgrade(key, "$ABILITY_CDN/$key.png", kind)
        }
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

    private fun formatPicks(total: Long): String = when {
        total >= 1_000_000 -> "${round1(total / 1_000_000.0)}M"
        total >= 1_000 -> "${round1(total / 1_000.0)}K"
        else -> total.toString()
    }
}
