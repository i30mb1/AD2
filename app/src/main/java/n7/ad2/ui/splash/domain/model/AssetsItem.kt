package n7.ad2.ui.splash.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssetsItem(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "section")
    val section: String = "",
)