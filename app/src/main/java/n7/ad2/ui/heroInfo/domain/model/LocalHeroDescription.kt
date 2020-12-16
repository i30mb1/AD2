package n7.ad2.ui.heroInfo.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalHeroDescription(
    @Json(name = "abilities")
    val abilities: List<Ability> = listOf(),
    @Json(name = "description")
    val description: String = "",
    @Json(name = "history")
    val history: String = "",
    @Json(name = "mainAttributes")
    val mainAttributes: MainAttribute = MainAttribute(),
    @Json(name = "talentTips")
    val talentTips: List<String> = listOf(),
    @Json(name = "talents")
    val talents: List<String> = listOf(),
    @Json(name = "trivia")
    val trivia: List<String> = listOf(),
)

@JsonClass(generateAdapter = true)
data class Ability(
    @Json(name = "cooldown")
    val cooldown: String? = null,
    @Json(name = "description")
    val description: String = "",
    @Json(name = "effects")
    val effects: List<String> = listOf(),
    @Json(name = "hotKey")
    val hotKey: String? = null,
    @Json(name = "itemBehaviour")
    val itemBehaviour: List<String> = listOf(),
    @Json(name = "legacyKey")
    val legacyKey: String? = null,
    @Json(name = "mana")
    val mana: String? = null,
    @Json(name = "notes")
    val notes: List<String>? = null,
    @Json(name = "params")
    val params: List<String>? = null,
    @Json(name = "audioUrl")
    val audioUrl: String? = null,
    @Json(name = "spellName")
    val spellName: String = "",
    @Json(name = "story")
    val story: String? = null,
)

@JsonClass(generateAdapter = true)
data class MainAttribute(
    @Json(name = "attrAgility")
    val attrAgility: Double = 0.0,
    @Json(name = "attrAgilityInc")
    val attrAgilityInc: Double = 0.0,
    @Json(name = "attrIntelligence")
    val attrIntelligence: Double = 0.0,
    @Json(name = "attrIntelligenceInc")
    val attrIntelligenceInc: Double = 0.0,
    @Json(name = "attrStrength")
    val attrStrength: Double = 0.0,
    @Json(name = "attrStrengthInc")
    val attrStrengthInc: Double = 0.0,
)