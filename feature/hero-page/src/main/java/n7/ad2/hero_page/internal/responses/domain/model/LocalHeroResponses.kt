package n7.ad2.hero_page.internal.responses.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalHeroResponsesItem(
    @Json(name = "category")
    val category: String = "",
    @Json(name = "responses")
    val responses: List<Response> = listOf()
)

@JsonClass(generateAdapter = true)
data class Response(
    @Json(name = "audioUrl")
    val audioUrl: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "icons")
    val icons: List<String> = emptyList(),
    @Json(name = "isArcane")
    val isArcane: Boolean = false
)