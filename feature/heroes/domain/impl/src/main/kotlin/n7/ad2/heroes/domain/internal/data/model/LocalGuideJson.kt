package n7.ad2.heroes.domain.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LocalGuideJson(
    @SerialName("heroName")
    val heroName: String,
    @SerialName("heroWinrate")
    val heroWinrate: String,
    @SerialName("heroPopularity")
    val heroPopularity: String,
    @SerialName("hardToWinHeroList")
    val hardToWinHeroList: List<HeroWithWinrate>,
    @SerialName("easyToWinHeroList")
    val easyToWinHeroList: List<HeroWithWinrate>,
    @SerialName("detailedGuide")
    val detailedGuide: DetailedGuide,
)

@Serializable
class HeroWithWinrate(
    @SerialName("heroName")
    val heroName: String,
    @SerialName("heroWinrate")
    val heroWinrate: Double,
)

@Serializable
class DetailedGuide(
    @SerialName("guideTime")
    val guideTime: String,
    @SerialName("heroStartingHeroItemsList")
    val heroStartingHeroItemsList: List<HeroItem>,
    @SerialName("heroItemsList")
    val heroItemsList: List<HeroItem>,
    @SerialName("heroSpellsList")
    val heroSpellsList: List<Spell>,
)

@Serializable
class Spell(
    @SerialName("spellName")
    val spellName: String,
    @SerialName("spellOrder")
    val spellOrder: String,
)

@Serializable
class HeroItem(
    @SerialName("itemName")
    val itemName: String,
    @SerialName("itemTime")
    val itemTime: String,
)
