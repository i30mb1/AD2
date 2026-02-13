package n7.ad2.itempage.internal.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalItemInfo(
    @SerialName("abilities")
    val abilities: List<Ability>? = null,
    @SerialName("additionalInformation")
    val additionalInformation: List<String>? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("lore")
    val lore: List<String>? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("cost")
    val cost: String? = null,
    @SerialName("boughtFrom")
    val boughtFrom: String? = null,
    @SerialName("tips")
    val tips: List<String>? = null,
    @SerialName("consistFrom")
    val consistFrom: List<String>? = null,
    @SerialName("trivia")
    val trivia: List<String>? = null,
    @SerialName("bonuses")
    val bonuses: List<String>? = null,
)

@Serializable
data class Ability(
    @SerialName("abilityName")
    val abilityName: String? = null,
    @SerialName("audioUrl")
    val audioUrl: String? = null,
    @SerialName("cooldown")
    val cooldown: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("effects")
    val effects: List<String>? = null,
    @SerialName("itemBehaviour")
    val itemBehaviour: List<String>? = null,
    @SerialName("mana")
    val mana: String? = null,
    @SerialName("notes")
    val notes: List<String>? = null,
    @SerialName("params")
    val params: List<String>? = null,
    @SerialName("story")
    val story: String? = null,
)
