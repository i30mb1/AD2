package n7.ad2.ui.heroInfo.domain.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class LocalHeroDescription(
    @Json(name = "abilities")
    val abilities: List<Ability> = listOf(),
    @Json(name = "description")
    val description: String = "",
    @Json(name = "history")
    val history: String = "",
    @Json(name = "mainAttributes")
    val mainAttributes: List<MainAttribute> = listOf(),
    @Json(name = "talentTips")
    val talentTips: List<String> = listOf(),
    @Json(name = "talents")
    val talents: List<String> = listOf(),
    @Json(name = "trivia")
    val trivia: List<String> = listOf()
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
    val notes: List<String> = listOf(),
        @Json(name = "params")
    val params: List<String> = listOf(),
        @Json(name = "audioUrl")
    val audioUrl: String? = null,
        @Json(name = "spellName")
    val spellName: String = "",
        @Json(name = "story")
    val story: String? = null
)

@JsonClass(generateAdapter = true)
data class MainAttribute(
    @Json(name = "attrAgility")
    val attrAgility: String = "",
    @Json(name = "attrAgilityInc")
    val attrAgilityInc: String = "",
    @Json(name = "attrIntelligence")
    val attrIntelligence: String = "",
    @Json(name = "attrIntelligenceInc")
    val attrIntelligenceInc: String = "",
    @Json(name = "attrStrength")
    val attrStrength: String = "",
    @Json(name = "attrStrengthInc")
    val attrStrengthInc: String = ""
)