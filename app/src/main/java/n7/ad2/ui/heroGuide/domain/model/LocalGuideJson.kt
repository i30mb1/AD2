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
    val hardToWinHeroList: List<String>,
    @Json(name = "easyToWinHeroList")
    val easyToWinHeroList: List<String>,
    @Json(name = "detailedGuide")
    val detailedGuide: DetailedGuide
)

@JsonClass(generateAdapter = true)
class DetailedGuide(
    @Json(name = "guideTime")
    val guideTime: String,
    @Json(name = "heroItemBuild")
    val heroItemBuild: List<ItemBuild>,
    @Json(name = "heroSkillBuild")
    val heroSkillBuild: List<String>
)

@JsonClass(generateAdapter = true)
class ItemBuild(
    @Json(name = "itemName")
    val itemName: List<String>,
    @Json(name = "itemTime")
    val itemTime: List<String>
)