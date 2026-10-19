package n7.ad2.heroes.domain.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `heroes/<hero>/<locale>/description.json`, produced by core/parser (schema 2). */
@Serializable
internal data class HeroDescriptionJson(
    @SerialName("schema") val schema: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("primaryAttribute") val primaryAttribute: String = "",
    @SerialName("attackType") val attackType: String = "",
    @SerialName("complexity") val complexity: Int = 1,
    @SerialName("roles") val roles: List<RoleJson> = emptyList(),
    @SerialName("short") val short: String = "",
    @SerialName("hype") val hype: String = "",
    @SerialName("bio") val bio: String = "",
    @SerialName("attributes") val attributes: AttributesJson = AttributesJson(),
    @SerialName("stats") val stats: StatsJson = StatsJson(),
    @SerialName("abilities") val abilities: List<AbilityJson> = emptyList(),
    @SerialName("talents") val talents: List<TalentJson> = emptyList(),
    @SerialName("facets") val facets: List<FacetJson> = emptyList(),
    @SerialName("trivia") val trivia: List<String> = emptyList(),
    @SerialName("wiki") val wiki: WikiJson = WikiJson(),
    @SerialName("changes") val changes: List<PatchChangeJson> = emptyList(),
)

@Serializable
internal data class WikiJson(
    @SerialName("quote") val quote: String? = null,
    @SerialName("releaseDate") val releaseDate: String? = null,
    @SerialName("attackAnimation") val attackAnimation: String? = null,
    @SerialName("collisionRadius") val collisionRadius: String? = null,
    @SerialName("recommendedItems") val recommendedItems: WikiArticleJson? = null,
    @SerialName("counters") val counters: WikiArticleJson? = null,
    @SerialName("guide") val guide: GuideJson? = null,
)

@Serializable
internal data class WikiArticleJson(
    @SerialName("lang") val lang: String = "",
    @SerialName("sections") val sections: List<WikiSectionJson> = emptyList(),
)

@Serializable
internal data class WikiSectionJson(
    @SerialName("title") val title: String = "",
    @SerialName("level") val level: Int = 2,
    @SerialName("entries") val entries: List<WikiEntryJson> = emptyList(),
)

@Serializable
internal data class WikiEntryJson(
    @SerialName("name") val name: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("lines") val lines: List<String> = emptyList(),
)

@Serializable
internal data class GuideJson(
    @SerialName("playstyle") val playstyle: PlaystyleJson? = null,
    @SerialName("tips") val tips: WikiArticleJson? = null,
)

@Serializable
internal data class PlaystyleJson(
    @SerialName("lang") val lang: String = "",
    @SerialName("text") val text: String = "",
    @SerialName("pros") val pros: List<String> = emptyList(),
    @SerialName("cons") val cons: List<String> = emptyList(),
)

@Serializable
internal data class WikiNotesJson(
    @SerialName("lang") val lang: String = "",
    @SerialName("notes") val notes: List<WikiNoteJson> = emptyList(),
)

@Serializable
internal data class WikiNoteJson(
    @SerialName("text") val text: String = "",
    @SerialName("tag") val tag: String? = null,
    @SerialName("indent") val indent: Int = 0,
)

@Serializable
internal data class PatchChangeJson(
    @SerialName("patch") val patch: String = "",
    @SerialName("timestamp") val timestamp: Long = 0,
    @SerialName("hero") val hero: List<PatchNoteJson> = emptyList(),
    @SerialName("talents") val talents: List<PatchNoteJson> = emptyList(),
    @SerialName("abilities") val abilities: List<AbilityPatchJson> = emptyList(),
)

@Serializable
internal data class AbilityPatchJson(
    @SerialName("name") val name: String = "",
    @SerialName("notes") val notes: List<PatchNoteJson> = emptyList(),
)

@Serializable
internal data class PatchNoteJson(
    @SerialName("text") val text: String = "",
    @SerialName("indent") val indent: Int = 0,
)

@Serializable
internal data class RoleJson(
    @SerialName("role") val role: String,
    @SerialName("level") val level: Int,
)

@Serializable
internal data class AttributesJson(
    @SerialName("str") val strength: Double = 0.0,
    @SerialName("strGain") val strengthGain: Double = 0.0,
    @SerialName("agi") val agility: Double = 0.0,
    @SerialName("agiGain") val agilityGain: Double = 0.0,
    @SerialName("int") val intelligence: Double = 0.0,
    @SerialName("intGain") val intelligenceGain: Double = 0.0,
)

@Serializable
internal data class StatsJson(
    @SerialName("health") val health: Double = 0.0,
    @SerialName("healthRegen") val healthRegen: Double = 0.0,
    @SerialName("mana") val mana: Double = 0.0,
    @SerialName("manaRegen") val manaRegen: Double = 0.0,
    @SerialName("armor") val armor: Double = 0.0,
    @SerialName("magicResist") val magicResist: Double = 0.0,
    @SerialName("damageMin") val damageMin: Double = 0.0,
    @SerialName("damageMax") val damageMax: Double = 0.0,
    @SerialName("attackRate") val attackRate: Double = 0.0,
    @SerialName("attackRange") val attackRange: Double = 0.0,
    @SerialName("projectileSpeed") val projectileSpeed: Double = 0.0,
    @SerialName("moveSpeed") val moveSpeed: Double = 0.0,
    @SerialName("turnRate") val turnRate: Double = 0.0,
    @SerialName("visionDay") val visionDay: Double = 0.0,
    @SerialName("visionNight") val visionNight: Double = 0.0,
)

@Serializable
internal data class AbilityJson(
    @SerialName("key") val key: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("image") val image: String = "",
    @SerialName("kind") val kind: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("lore") val lore: String = "",
    @SerialName("notes") val notes: List<String> = emptyList(),
    @SerialName("scepter") val scepter: String? = null,
    @SerialName("shard") val shard: String? = null,
    @SerialName("behavior") val behavior: List<String> = emptyList(),
    @SerialName("targetTeam") val targetTeam: String? = null,
    @SerialName("damageType") val damageType: String? = null,
    @SerialName("spellImmunity") val spellImmunity: String? = null,
    @SerialName("dispellable") val dispellable: String? = null,
    @SerialName("cooldown") val cooldown: String? = null,
    @SerialName("manaCost") val manaCost: String? = null,
    @SerialName("healthCost") val healthCost: String? = null,
    @SerialName("goldCost") val goldCost: String? = null,
    @SerialName("castRange") val castRange: String? = null,
    @SerialName("castPoint") val castPoint: String? = null,
    @SerialName("channelTime") val channelTime: String? = null,
    @SerialName("duration") val duration: String? = null,
    @SerialName("params") val params: List<AbilityParamJson> = emptyList(),
    @SerialName("wikiNotes") val wikiNotes: WikiNotesJson? = null,
)

@Serializable
internal data class AbilityParamJson(
    @SerialName("label") val label: String = "",
    @SerialName("value") val value: String? = null,
    @SerialName("talent") val talent: String? = null,
    @SerialName("shard") val shard: String? = null,
    @SerialName("scepter") val scepter: String? = null,
)

@Serializable
internal data class TalentJson(
    @SerialName("level") val level: Int,
    @SerialName("left") val left: String = "",
    @SerialName("right") val right: String = "",
)

@Serializable
internal data class FacetJson(
    @SerialName("name") val name: String = "",
    @SerialName("description") val description: String = "",
)
