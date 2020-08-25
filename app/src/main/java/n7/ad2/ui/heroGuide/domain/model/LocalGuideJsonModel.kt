package n7.ad2.ui.heroGuide.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class LocalGuideJsonModel(
    @Json(name = "heroName")
    val heroName: String,
    @Json(name = "heroWinRate")
    val heroWinRate: String,
    @Json(name = "heroLoseRate")
    val heroLoseRate: String,
    @Json(name = "heroBestVersus")
    val heroBestVersus: List<String>,
    @Json(name = "heroWorstVersus")
    val heroWorstVersus: List<String>,
    @Json(name = "heroItemBuild")
    val heroItemBuild: List<String>,
    @Json(name = "heroSkillBuild")
    val heroSkillBuild: List<String>
)