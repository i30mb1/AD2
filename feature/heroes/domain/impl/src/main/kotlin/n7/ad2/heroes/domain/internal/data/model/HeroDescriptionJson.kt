package n7.ad2.heroes.domain.internal.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HeroDescriptionJson(
    @Json(name = "abilities")
    val abilities: List<Ability> = listOf(),
    @Json(name = "description")
    val description: String = "",
    @Json(name = "history")
    val history: String = "",
    @Json(name = "mainAttributes")
    val mainAttributes: MainAttribute = MainAttribute(),
    @Json(name = "trivia")
    val trivia: List<String>? = null,
)

@JsonClass(generateAdapter = true)
data class Ability(
    @Json(name = "cooldown")
    val cooldown: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "effects")
    val effects: List<String> = listOf(),
    @Json(name = "hot_key")
    val hotKey: String? = null,
    @Json(name = "item_behaviour")
    val itemBehaviour: List<String> = listOf(),
    @Json(name = "legacy_key")
    val legacyKey: String? = null,
    @Json(name = "mana")
    val mana: String? = null,
    @Json(name = "notes")
    val notes: List<String>? = null,
    @Json(name = "params")
    val params: List<String>? = null,
    @Json(name = "audio_url")
    val audioUrl: String? = null,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "story")
    val story: String? = null,
    @Json(name = "talents")
    val talents: List<Talent>? = null,
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

@JsonClass(generateAdapter = true)
data class Talent(
    @Json(name = "talentLeft")
    val talentLeft: String = "",
    @Json(name = "talentLvl")
    val talentLvl: String = "",
    @Json(name = "talentRight")
    val talentRight: String = "",
)
