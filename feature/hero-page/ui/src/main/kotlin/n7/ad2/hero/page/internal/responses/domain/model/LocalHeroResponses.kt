package n7.ad2.hero.page.internal.responses.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalHeroResponsesItem(
    @SerialName("category")
    val category: String = "",
    @SerialName("responses")
    val responses: List<Response> = listOf(),
)

@Serializable
data class Response(
    @SerialName("audioUrl")
    val audioUrl: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("icons")
    val icons: List<String> = emptyList(),
    @SerialName("isArcane")
    val isArcane: Boolean = false,
)
