package n7.ad2.ui.splash.domain.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class AssetsItem(
    @Json(name = "abilities")
    val abilities: List<Ability> = listOf(),
    @Json(name = "additionalInformation")
    val additionalInformation: List<String> = listOf(),
    @Json(name = "description")
    val description: String = "",
    @Json(name = "lore")
    val lore: List<Any> = listOf(),
    @Json(name = "name")
    val name: String = "",
    @Json(name = "tips")
    val tips: List<String> = listOf()
)

@JsonClass(generateAdapter = true)
data class Ability(
    @Json(name = "abilityName")
    val abilityName: String = "",
    @Json(name = "audioUrl")
    val audioUrl: String? = null,
    @Json(name = "cooldown")
    val cooldown: Any? = null,
    @Json(name = "description")
    val description: String = "",
    @Json(name = "effects")
    val effects: List<String> = listOf(),
    @Json(name = "itemBehaviour")
    val itemBehaviour: List<Any> = listOf(),
    @Json(name = "mana")
    val mana: String? = null,
    @Json(name = "notes")
    val notes: List<String> = listOf(),
    @Json(name = "params")
    val params: List<String> = listOf(),
    @Json(name = "story")
    val story: Any? = null
)