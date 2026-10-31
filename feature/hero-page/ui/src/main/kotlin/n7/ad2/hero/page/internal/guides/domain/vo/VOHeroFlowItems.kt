package n7.ad2.hero.page.internal.guides.domain.vo

/** Everything the "Guides" tab shows for one hero, built from the latest OpenDota guide. */
data class VOGuide(
    val winrate: String,
    val matches: String,
    val proPicks: Int,
    val proWinrate: String?,
    val proBans: Int,
    val rankWinrates: List<VOGuideBar>,
    val durationWinrates: List<VOGuideBar>,
    val skillBuild: VOSkillBuild?,
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

/** One bar of a win-rate chart, [label] is a rank or a match length. */
data class VOGuideBar(val label: Label, val winrate: Double) {
    sealed interface Label {
        /** OpenDota skill bracket 1..8. */
        data class Rank(val rank: Int) : Label
        data class Minutes(val from: Int, val to: Int?) : Label
    }
}

/** Grid "ability x skill point": every row lists the 1-based point numbers spent on it, talents and attribute points get a row each. */
data class VOSkillBuild(val levels: Int, val rows: List<VOSkillRow>)

data class VOSkillRow(val imageUrl: String, val kind: Kind, val levels: Set<Int>) {
    enum class Kind { ABILITY, TALENT, ATTRIBUTES }
}
