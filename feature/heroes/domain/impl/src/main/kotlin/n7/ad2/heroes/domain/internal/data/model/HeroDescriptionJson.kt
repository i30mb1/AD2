package n7.ad2.heroes.domain.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeroDescriptionJson(
    @SerialName("abilities")
    val abilities: List<Ability> = listOf(),
    @SerialName("description")
    val description: String = "",
    @SerialName("history")
    val history: String = "",
    @SerialName("mainAttributes")
    val mainAttributes: MainAttribute = MainAttribute(),
    @SerialName("trivia")
    val trivia: List<String>? = null,
)

@Serializable
data class Ability(
    @SerialName("cooldown")
    val cooldown: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("effects")
    val effects: List<String> = listOf(),
    @SerialName("hot_key")
    val hotKey: String? = null,
    @SerialName("item_behaviour")
    val itemBehaviour: List<String> = listOf(),
    @SerialName("legacy_key")
    val legacyKey: String? = null,
    @SerialName("mana")
    val mana: String? = null,
    @SerialName("notes")
    val notes: List<String>? = null,
    @SerialName("params")
    val params: List<String>? = null,
    @SerialName("audio_url")
    val audioUrl: String? = null,
    @SerialName("name")
    val name: String = "",
    @SerialName("story")
    val story: String? = null,
    @SerialName("talents")
    val talents: List<Talent>? = null,
)

@Serializable
data class MainAttribute(
    @SerialName("attrAgility")
    val attrAgility: Double = 0.0,
    @SerialName("attrAgilityInc")
    val attrAgilityInc: Double = 0.0,
    @SerialName("attrIntelligence")
    val attrIntelligence: Double = 0.0,
    @SerialName("attrIntelligenceInc")
    val attrIntelligenceInc: Double = 0.0,
    @SerialName("attrStrength")
    val attrStrength: Double = 0.0,
    @SerialName("attrStrengthInc")
    val attrStrengthInc: Double = 0.0,
)

@Serializable
data class Talent(
    @SerialName("talentLeft")
    val talentLeft: String = "",
    @SerialName("talentLvl")
    val talentLvl: String = "",
    @SerialName("talentRight")
    val talentRight: String = "",
)
