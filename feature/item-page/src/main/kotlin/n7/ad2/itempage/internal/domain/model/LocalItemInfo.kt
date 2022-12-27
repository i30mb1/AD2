package n7.ad2.itempage.internal.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalItemInfo(
    @Json(name = "abilities")
    val abilities: List<Ability>? = null,
    @Json(name = "additionalInformation")
    val additionalInformation: List<String>? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "lore")
    val lore: List<String>? = null,
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "cost")
    val cost: String? = null,
    @Json(name = "boughtFrom")
    val boughtFrom: String? = null,
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
    val abilityName: String? = null,
    @Json(name = "audioUrl")
    val audioUrl: String? = null,
    @Json(name = "cooldown")
    val cooldown: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "effects")
    val effects: List<String>? = null,
    @Json(name = "itemBehaviour")
    val itemBehaviour: List<String>? = null,
    @Json(name = "mana")
    val mana: String? = null,
    @Json(name = "notes")
    val notes: List<String>? = null,
    @Json(name = "params")
    val params: List<String>? = null,
    @Json(name = "story")
    val story: String? = null,
)