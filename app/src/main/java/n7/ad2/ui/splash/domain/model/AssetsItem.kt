package n7.ad2.ui.splash.domain.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class AssetsItemList(
    @Json(name = "items")
    val items: List<AssetsItem> = listOf()
)

@JsonClass(generateAdapter = true)
data class AssetsItem(
    @Json(name = "assetsPath")
    val assetsPath: String = "",
    @Json(name = "nameEng")
    val nameEng: String = "",
    @Json(name = "section")
    val section: String = ""
)