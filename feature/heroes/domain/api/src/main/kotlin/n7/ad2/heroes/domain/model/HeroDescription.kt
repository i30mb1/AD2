package n7.ad2.heroes.domain.model

data class HeroDescription(
    val abilities: List<Ability> = listOf(),
    val description: String = "",
    val history: String = "",
    val mainAttributes: MainAttribute = MainAttribute(),
    val trivia: List<String>? = null,
)

data class Ability(
    val imageUrl: String,
    val cooldown: String? = null,
    val description: String? = null,
    val effects: List<String> = listOf(),
    val hotKey: String? = null,
    val itemBehaviour: List<String> = listOf(),
    val legacyKey: String? = null,
    val mana: String? = null,
    val notes: List<String>? = null,
    val params: List<String>? = null,
    val audioUrl: String? = null,
    val name: String = "",
    val story: String? = null,
    val talents: List<Talent>? = null,
)

data class MainAttribute(
    val attrAgility: Double = 0.0,
    val attrAgilityInc: Double = 0.0,
    val attrIntelligence: Double = 0.0,
    val attrIntelligenceInc: Double = 0.0,
    val attrStrength: Double = 0.0,
    val attrStrengthInc: Double = 0.0,
)

data class Talent(
    val talentLeft: String = "",
    val talentLvl: String = "",
    val talentRight: String = "",
)
