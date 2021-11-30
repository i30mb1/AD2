package n7.ad2.database_guides.internal.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AssetsHero(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "main_attribute")
    val mainAttribute: String = "",
)