package n7.ad2.database_guides.internal.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AssetsItem(
    @SerialName("name")
    val name: String = "",
    @SerialName("section")
    val section: String = "",
)
