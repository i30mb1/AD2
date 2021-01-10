package n7.ad2.ui.splash.domain.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class AssetsItem(
    @Json(name = "path")
    val path: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "section")
    val section: String = ""
)