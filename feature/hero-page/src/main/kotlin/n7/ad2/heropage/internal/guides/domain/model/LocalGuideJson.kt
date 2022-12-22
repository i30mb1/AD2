package n7.ad2.heropage.internal.guides.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class LocalGuideJson(
    @Json(name = "heroName")
    val heroName: String,
    @Json(name = "heroWinrate")
    val heroWinrate: String,
    @Json(name = "heroPopularity")
    val heroPopularity: String,
    @Json(name = "hardToWinHeroList")
    val hardToWinHeroList: List<HeroWithWinrate>,
    @Json(name = "easyToWinHeroList")
    val easyToWinHeroList: List<HeroWithWinrate>,
    @Json(name = "detailedGuide")
    val detailedGuide: DetailedGuide,
)

@JsonClass(generateAdapter = true)
class HeroWithWinrate(
    @Json(name = "heroName")
    val heroName: String,
    @Json(name = "heroWinrate")
    val heroWinrate: Double,
)

@JsonClass(generateAdapter = true)
class DetailedGuide(
    @Json(name = "guideTime")
    val guideTime: String,
    @Json(name = "heroStartingHeroItemsList")
    val heroStartingHeroItemsList: List<HeroItem>,
    @Json(name = "heroItemsList")
    val heroItemsList: List<HeroItem>,
    @Json(name = "heroSpellsList")
    val heroSpellsList: List<Spell>,
)

@JsonClass(generateAdapter = true)
class Spell(
    @Json(name = "spellName")
    val spellName: String,
    @Json(name = "spellOrder")
    val spellOrder: String,
)

@JsonClass(generateAdapter = true)
class HeroItem(
    @Json(name = "itemName")
    val itemName: String,
    @Json(name = "itemTime")
    val itemTime: String = "",
)