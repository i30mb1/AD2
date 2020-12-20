package n7.ad2.ui.itemInfo.domain.model

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class LocalItemInfo(
    @Json(name = "abilities")
    val abilities: List<Ability>? = null,
    @Json(name = "additionalInformation")
    val additionalInformation: List<String>? = null,
    @Json(name = "description")
    val description: String = "",
    @Json(name = "lore")
    val lore: List<String>? = null,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "cost")
    val cost: String = "",
    @Json(name = "boughtFrom")
    val boughtFrom: String = "",
    @Json(name = "tips")
    val tips: List<String>? = null,
    @Json(name = "consistFrom")
    val consistFrom: List<String>? = null,
    @Json(name = "trivia")
    val trivia: List<String>? = null,
    @Json(name = "bonuses")
    val bonuses: List<String>? = null,
)

@JsonClass(generateAdapter = true)
data class Ability(
    @Json(name = "abilityName")
    val abilityName: String = "",
    @Json(name = "audioUrl")
    val audioUrl: String? = null,
    @Json(name = "cooldown")
    val cooldown: String? = null,
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
    val story: String? = null,
)