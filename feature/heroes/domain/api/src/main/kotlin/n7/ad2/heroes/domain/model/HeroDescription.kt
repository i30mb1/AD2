package n7.ad2.heroes.domain.model

/**
 * Text fields may contain `<b>` and `<font color='#rrggbb'>` tags, line breaks are `\n`.
 */
data class HeroDescription(
    val name: String = "",
    val primaryAttribute: HeroAttribute = HeroAttribute.UNIVERSAL,
    val attackType: AttackType = AttackType.MELEE,
    val complexity: Int = 1,
    val roles: List<HeroRole> = listOf(),
    val short: String = "",
    val hype: String = "",
    val bio: String = "",
    val attributes: HeroAttributes = HeroAttributes(),
    val stats: HeroStats = HeroStats(),
    val abilities: List<Ability> = listOf(),
    val talents: List<TalentLevel> = listOf(),
    val facets: List<Facet> = listOf(),
    val trivia: List<String> = listOf(),
    val wiki: HeroWiki = HeroWiki(),
    /** Latest patches that changed the hero, newest first. */
    val changes: List<PatchChange> = listOf(),
)

/** What only the community wiki has. It lags behind the game, numbers there can be outdated. */
data class HeroWiki(
    val quote: String? = null,
    val releaseDate: String? = null,
    val attackAnimation: String? = null,
    val collisionRadius: String? = null,
    val recommendedItems: WikiArticle? = null,
    val counters: WikiArticle? = null,
    val playstyle: Playstyle? = null,
    val tips: WikiArticle? = null,
)

/**
 * [isFallbackLanguage] is true when the wiki of the app language has no such text and the English one is shown.
 */
data class WikiArticle(val isFallbackLanguage: Boolean, val sections: List<WikiSection>)

/** [level] 2 is a section, 3 and deeper are its parts, e.g. "Bad against" → "Items". */
data class WikiSection(val title: String, val level: Int, val entries: List<WikiEntry>)

/** [name] is the hero or item the lines are about, null for general lines. */
data class WikiEntry(val name: String?, val iconUrl: String?, val lines: List<String>)

data class Playstyle(val isFallbackLanguage: Boolean, val text: String, val pros: List<String>, val cons: List<String>)

data class WikiNotes(val isFallbackLanguage: Boolean, val notes: List<WikiNote>)

/** [tag] marks notes about talents, Aghanim's upgrades, formulas. [indent] is 1 for a note about the previous one. */
data class WikiNote(val text: String, val tag: String?, val indent: Int)

data class PatchChange(
    val patch: String,
    val timestampSeconds: Long,
    val hero: List<PatchNote>,
    val talents: List<PatchNote>,
    val abilities: List<AbilityPatchNotes>,
)

data class AbilityPatchNotes(val abilityName: String, val notes: List<PatchNote>)

data class PatchNote(val text: String, val indent: Int)

enum class HeroAttribute { STRENGTH, AGILITY, INTELLIGENCE, UNIVERSAL }

enum class AttackType { MELEE, RANGED }

enum class Role { CARRY, SUPPORT, NUKER, DISABLER, JUNGLER, DURABLE, ESCAPE, PUSHER, INITIATOR }

/** [level] is 1..3, how strong the hero is in this role. */
data class HeroRole(val role: Role, val level: Int)

data class HeroAttributes(
    val strength: Double = 0.0,
    val strengthGain: Double = 0.0,
    val agility: Double = 0.0,
    val agilityGain: Double = 0.0,
    val intelligence: Double = 0.0,
    val intelligenceGain: Double = 0.0,
)

/** Values at level 1, attributes already applied. */
data class HeroStats(
    val health: Double = 0.0,
    val healthRegen: Double = 0.0,
    val mana: Double = 0.0,
    val manaRegen: Double = 0.0,
    val armor: Double = 0.0,
    val magicResist: Double = 0.0,
    val damageMin: Double = 0.0,
    val damageMax: Double = 0.0,
    val attackRate: Double = 0.0,
    val attackRange: Double = 0.0,
    val projectileSpeed: Double = 0.0,
    val moveSpeed: Double = 0.0,
    val turnRate: Double = 0.0,
    val visionDay: Double = 0.0,
    val visionNight: Double = 0.0,
)

enum class AbilityKind { BASIC, ULTIMATE, INNATE, EXTRA, SCEPTER, SHARD }

enum class AbilityBehavior { PASSIVE, NO_TARGET, UNIT_TARGET, POINT_TARGET, AOE, CHANNELLED, TOGGLE, DIRECTIONAL, AUTOCAST }

enum class TargetTeam { ALLIES, ENEMIES, BOTH }

enum class DamageType { PHYSICAL, MAGICAL, PURE }

enum class SpellImmunity { YES, NO, ALLIES_YES_ENEMIES_NO }

enum class Dispellable { YES, STRONG, NO }

/**
 * Level dependent values are joined with "/", e.g. "8/7/6/5".
 * [mana] is the mana cost, the name is kept for the games feature.
 */
data class Ability(
    val name: String = "",
    val imageUrl: String = "",
    val kind: AbilityKind = AbilityKind.BASIC,
    val description: String = "",
    val lore: String = "",
    val notes: List<String> = listOf(),
    val scepter: String? = null,
    val shard: String? = null,
    val behavior: List<AbilityBehavior> = listOf(),
    val targetTeam: TargetTeam? = null,
    val damageType: DamageType? = null,
    val spellImmunity: SpellImmunity? = null,
    val dispellable: Dispellable? = null,
    val cooldown: String? = null,
    val mana: String? = null,
    val healthCost: String? = null,
    val goldCost: String? = null,
    val castRange: String? = null,
    val castPoint: String? = null,
    val channelTime: String? = null,
    val duration: String? = null,
    val params: List<AbilityParam> = listOf(),
    val wikiNotes: WikiNotes? = null,
)

/** [talent], [shard] and [scepter] are the values with the corresponding upgrade. */
data class AbilityParam(
    val label: String,
    val value: String? = null,
    val talent: String? = null,
    val shard: String? = null,
    val scepter: String? = null,
)

data class TalentLevel(val level: Int, val left: String, val right: String)

data class Facet(val name: String, val description: String)
