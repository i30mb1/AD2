package n7.ad2.heroes.domain.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeroDescriptionJson(
    @SerialName("abilities") val abilities: List<Ability> = emptyList(),
    @SerialName("description") val description: String = "",
    @SerialName("history") val history: String = "",
    @SerialName("mainAttributes") val mainAttributes: MainAttribute = MainAttribute(),
    @SerialName("trivia") val trivia: List<String>? = null,
    @SerialName("baseStats") val baseStats: BaseStats? = null,
    @SerialName("roles") val roles: List<String> = emptyList(),
    @SerialName("attackType") val attackType: String = "",
    @SerialName("complexity") val complexity: Int = 0,
    @SerialName("talents") val talents: HeroTalents? = null,
)

@Serializable
data class Ability(
    @SerialName("cooldown") val cooldown: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("effects") val effects: List<String> = emptyList(),
    @SerialName("hot_key") val hotKey: String? = null,
    @SerialName("item_behaviour") val itemBehaviour: List<String> = emptyList(),
    @SerialName("legacy_key") val legacyKey: String? = null,
    @SerialName("mana") val mana: String? = null,
    @SerialName("notes") val notes: List<String>? = null,
    @SerialName("params") val params: List<String>? = null,
    @SerialName("audioUrl") val audioUrl: String? = null,
    @SerialName("name") val name: String = "",
    @SerialName("story") val story: String? = null,
    @SerialName("is_innate") val isInnate: Boolean = false,
    @SerialName("aghanim_scepter") val aghanimScepter: String? = null,
    @SerialName("aghanim_shard") val aghanimShard: String? = null,
)

@Serializable
data class MainAttribute(
    @SerialName("attrAgility") val attrAgility: Double = 0.0,
    @SerialName("attrAgilityInc") val attrAgilityInc: Double = 0.0,
    @SerialName("attrIntelligence") val attrIntelligence: Double = 0.0,
    @SerialName("attrIntelligenceInc") val attrIntelligenceInc: Double = 0.0,
    @SerialName("attrStrength") val attrStrength: Double = 0.0,
    @SerialName("attrStrengthInc") val attrStrengthInc: Double = 0.0,
)

@Serializable
data class BaseStats(
    @SerialName("armor") val armor: Double = 0.0,
    @SerialName("damage") val damage: String = "",
    @SerialName("movementSpeed") val movementSpeed: Int = 0,
    @SerialName("attackSpeed") val attackSpeed: String = "",
    @SerialName("health") val health: String = "",
    @SerialName("mana") val mana: String = "",
)

@Serializable
data class HeroTalents(
    @SerialName("hero_talents") val heroTalents: List<TalentRow> = emptyList(),
    @SerialName("notes") val notes: List<String>? = null,
)

@Serializable
data class TalentRow(
    @SerialName("talent_left") val talentLeft: String = "",
    @SerialName("talent_lvl") val talentLvl: String = "",
    @SerialName("talent_right") val talentRight: String = "",
)
