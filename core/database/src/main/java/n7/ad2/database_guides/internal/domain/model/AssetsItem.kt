package n7.ad2.database_guides.internal.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AssetsItem(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "section")
    val section: String = "",
)