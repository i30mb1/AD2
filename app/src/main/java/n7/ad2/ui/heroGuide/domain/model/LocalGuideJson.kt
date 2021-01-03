package n7.ad2.ui.heroGuide.domain.model

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
    @Json(name = "startingItems")
    val heroStartingItemsList: List<String>,
    @Json(name = "heroItemBuild")
    val heroItemsList: List<ItemBuild>,
    @Json(name = "heroSkillBuild")
    val heroSkillsList: List<Skill>,
)

@JsonClass(generateAdapter = true)
class Skill(
    @Json(name = "skillName")
    val skillName: String,
    @Json(name = "skillOrder")
    val skillOrder: String,
)

@JsonClass(generateAdapter = true)
class ItemBuild(
    @Json(name = "itemName")
    val itemName: String,
    @Json(name = "itemTime")
    val itemTime: String,
)