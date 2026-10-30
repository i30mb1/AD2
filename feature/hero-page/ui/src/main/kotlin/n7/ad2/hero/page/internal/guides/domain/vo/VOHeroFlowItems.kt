package n7.ad2.hero.page.internal.guides.domain.vo

/** Everything the "Guides" tab shows for one hero, built from the latest OpenDota guide. */
data class VOGuide(
    val winrate: String,
    val matches: String,
    val startingItems: List<VOGuideItem>,
    val phases: List<VOGuidePhase>,
    val easyToWin: List<VOGuideHero>,
    val hardToWin: List<VOGuideHero>,
)

data class VOGuidePhase(val phase: Phase, val items: List<VOGuideItem>) {
    enum class Phase { EARLY, MID, LATE }
}

data class VOGuideItem(val itemName: String, val imageUrl: String)

/** [winrate] is how often the hero wins against [heroName], [winrateText] is the same number ready to show. */
data class VOGuideHero(val heroName: String, val imageUrl: String, val winrate: Double, val winrateText: String)
