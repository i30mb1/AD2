package n7.ad2.heroes.domain

class Guide(
    val heroName: String,
    val heroWinrate: String,
    val heroPopularity: String,
    val guideTime: String,
    val hardToWinHeroList: List<HeroWithWinrate>,
    val easyToWinHeroList: List<HeroWithWinrate>,
    val heroStartingHeroItemsList: List<HeroItem>,
    val heroItemsList: List<HeroItem>,
    val heroSpellsList: List<Spell>,
)

class HeroWithWinrate(
    val heroName: String,
    val heroWinrate: Double,
)

class Spell(
    val spellName: String,
    val spellOrder: String,
)

class HeroItem(
    val itemName: String,
    val itemTime: String,
)
