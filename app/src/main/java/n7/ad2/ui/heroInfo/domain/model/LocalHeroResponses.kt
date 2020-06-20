package n7.ad2.ui.heroInfo.domain.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

class LocalHeroResponses : ArrayList<HeroResponsesItem>()

@JsonClass(generateAdapter = true)
data class HeroResponsesItem(
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
    val title: String = ""
)