package n7.ad2.heroes.domain.model

class Guide(
    val heroName: String,
    val heroWinrate: String,
    val heroPopularity: String,
    val proPicks: Int,
    val proWins: Int,
    val proBans: Int,
    val rankWinrates: List<RankWinrate>,
    val durationWinrates: List<DurationWinrate>,
    val hardToWinHeroList: List<HeroWithWinrate>,
    val easyToWinHeroList: List<HeroWithWinrate>,
    val heroStartingHeroItemsList: List<HeroItem>,
    val heroItemsList: List<HeroItem>,
    val skillBuild: List<SkillUpgrade>,
)

class HeroWithWinrate(val heroName: String, val heroWinrate: Double, val avatarUrl: String)

class HeroItem(val itemName: String, val itemTime: String)

/** [rank] is the OpenDota skill bracket: 1 Herald … 8 Immortal. */
class RankWinrate(val rank: Int, val winrate: Double)

/** Win-rate of matches that lasted from [fromMinute] up to [toMinute], the last group has no upper bound. */
class DurationWinrate(val fromMinute: Int, val toMinute: Int?, val winrate: Double)

/** One skill point of the popular build, in the order it is spent. [abilityKey] is the internal name, e.g. juggernaut_blade_fury. */
class SkillUpgrade(val abilityKey: String, val imageUrl: String, val kind: Kind) {
    /** [ATTRIBUTES] is the +2 to all attributes point, it is a special_bonus in the game data but not a talent. */
    enum class Kind { ABILITY, TALENT, ATTRIBUTES }
}
