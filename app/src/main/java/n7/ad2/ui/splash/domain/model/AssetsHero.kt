package n7.ad2.ui.splash.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AssetsHeroList(
    @Json(name = "heroes")
    val heroes: List<AssetsHero> = listOf(),
)

@JsonClass(generateAdapter = true)
data class AssetsHero(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "mainAttribute")
    val mainAttribute: String = "",
)