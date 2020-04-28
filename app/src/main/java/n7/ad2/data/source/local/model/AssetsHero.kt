package n7.ad2.data.source.local.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AssetsHeroList(
        @Json(name = "heroes")
        val heroes: List<AssetsHero> = listOf()
)

@JsonClass(generateAdapter = true)
data class AssetsHero(
        @Json(name = "nameEng")
        val name: String = "",
        @Json(name = "assetsPath")
        val assetsPath: String = "",
        @Json(name = "mainAttr")
        val mainAttr: String = ""
)