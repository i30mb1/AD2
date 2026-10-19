package n7.ad2.parser.heroes.valve

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Converts one hero from the official datafeed (https://www.dota2.com/datafeed/herodata) into the
 * `description.json` format the app reads (schema 2).
 *
 * The datafeed keeps numbers apart from text: descriptions contain `%special_value%` placeholders and
 * talents contain `{s:bonus_special_value}` placeholders, both are resolved here. Enum-like values
 * (roles, damage type, behavior...) are written as stable keys, the app maps them to its own strings.
 */
internal object ValveHeroParser {

    const val SCHEMA_VERSION = 3

    // DOTA_ABILITY_BEHAVIOR bits, checked against known abilities: Earth Spike = point|unit, Mana Drain = unit|channelled, Rot = toggle
    private val behaviorFlags = listOf(
        2L to "passive",
        4L to "no_target",
        8L to "unit_target",
        16L to "point_target",
        32L to "aoe",
        128L to "channelled",
        512L to "toggle",
        1024L to "directional",
        4096L to "autocast",
    )
    private const val BEHAVIOR_HIDDEN = 1L

    private val roles = listOf("carry", "support", "nuker", "disabler", "jungler", "durable", "escape", "pusher", "initiator")
    private val attributes = listOf("strength", "agility", "intelligence", "universal")

    /** Values the datafeed duplicates into special_values, they are already shown in the ability header. */
    private val headerValueNames = setOf("AbilityCastRange", "AbilityCastPoint", "AbilityManaCost", "AbilityCooldown", "AbilityChannelTime", "AbilityDuration", "AbilityDamage", "AbilityHealthCost")

    fun parse(hero: JSONObject, imagePath: (abilityKey: String, isInnate: Boolean) -> String): JSONObject {
        val abilities = hero.array("abilities").objects()
        val talents = hero.array("talents").objects()
        val hasFacets = hero.array("facets").isNotEmpty()

        return JSONObject().apply {
            put("schema", SCHEMA_VERSION)
            put("id", hero.long("id"))
            put("key", hero.string("name"))
            put("name", hero.string("name_loc"))
            put("primaryAttribute", attributes.getOrElse(hero.long("primary_attr").toInt()) { "universal" })
            put("attackType", if (hero.long("attack_capability") == 2L) "ranged" else "melee")
            put("complexity", hero.long("complexity"))
            put("roles", hero.array("role_levels").mapIndexedNotNull { index, level ->
                val value = (level as Number).toLong()
                if (value <= 0 || index !in roles.indices) return@mapIndexedNotNull null
                JSONObject().apply {
                    put("role", roles[index])
                    put("level", value)
                }
            }.toJsonArray())
            put("short", hero.string("npe_desc_loc").cleanText())
            put("hype", hero.string("hype_loc").cleanText())
            put("bio", hero.string("bio_loc").cleanText())
            put("attributes", JSONObject().apply {
                put("str", hero.double("str_base"))
                put("strGain", hero.double("str_gain"))
                put("agi", hero.double("agi_base"))
                put("agiGain", hero.double("agi_gain"))
                put("int", hero.double("int_base"))
                put("intGain", hero.double("int_gain"))
            })
            put("stats", JSONObject().apply {
                put("health", hero.double("max_health"))
                put("healthRegen", hero.double("health_regen").round(2))
                put("mana", hero.double("max_mana"))
                put("manaRegen", hero.double("mana_regen").round(2))
                put("armor", hero.double("armor").round(1))
                put("magicResist", hero.double("magic_resistance"))
                put("damageMin", hero.double("damage_min"))
                put("damageMax", hero.double("damage_max"))
                put("attackRate", hero.double("attack_rate").round(2))
                put("attackRange", hero.double("attack_range"))
                put("projectileSpeed", hero.double("projectile_speed"))
                put("moveSpeed", hero.double("movement_speed"))
                put("turnRate", hero.double("turn_rate").round(2))
                put("visionDay", hero.double("sight_range_day"))
                put("visionNight", hero.double("sight_range_night"))
            })
            val heroValues = abilities.flatMap { ability -> ability.array("special_values").objects() }
            put("abilities", abilities.map { ability -> parseAbility(ability, heroValues, hasFacets, imagePath) }.toJsonArray())
            put("talents", parseTalents(talents, abilities))
            put("facets", hero.array("facets").objects().map { facet -> parseFacet(facet) }.toJsonArray())
        }
    }

    fun abilityKeys(hero: JSONObject): List<String> = hero.array("abilities").objects().map { it.string("name") }

    fun abilityNamesById(hero: JSONObject): Map<Long, String> = hero.array("abilities").objects().associate { it.long("id") to it.string("name_loc") }

    // ===== abilities =====

    private fun parseAbility(ability: JSONObject, heroValues: List<JSONObject>, hasFacets: Boolean, imagePath: (String, Boolean) -> String): JSONObject {
        val values = ability.array("special_values").objects()
        val behavior = ability.long("behavior")
        val isInnate = ability.bool("ability_is_innate")
        val kind = when {
            isInnate -> "innate"
            ability.bool("ability_is_granted_by_scepter") -> "scepter"
            ability.bool("ability_is_granted_by_shard") -> "shard"
            behavior and BEHAVIOR_HIDDEN != 0L -> "extra"
            ability.long("type") == 1L -> "ultimate"
            else -> "basic"
        }
        return JSONObject().apply {
            put("key", ability.string("name"))
            put("name", ability.string("name_loc"))
            put("image", imagePath(ability.string("name"), isInnate))
            put("kind", kind)
            put("description", ability.string("desc_loc").resolve(values, heroValues, ValueSource.BASE))
            put("lore", ability.string("lore_loc").cleanText())
            // a note is a standalone fact, without its number it says nothing
            put("notes", ability.array("notes_loc").map { (it as String).resolve(values, heroValues, ValueSource.BASE, unknown = MISSING) }.filter { it.isNotEmpty() && MISSING !in it }.toJsonArray())
            put("scepter", ability.string("scepter_loc").resolve(values, heroValues, ValueSource.SCEPTER).ifEmpty { null })
            put("shard", ability.string("shard_loc").resolve(values, heroValues, ValueSource.SHARD).ifEmpty { null })
            put("behavior", behaviorFlags.filter { (flag, _) -> behavior and flag != 0L }.map { it.second }.toJsonArray())
            put("targetTeam", targetTeam(ability.long("target_team")))
            put("damageType", damageType(ability.long("damage")))
            put("spellImmunity", spellImmunity(ability.long("immunity")))
            put("dispellable", dispellable(ability.long("dispellable")))
            put("cooldown", ability.array("cooldowns").formatLevels())
            put("manaCost", ability.array("mana_costs").formatLevels())
            put("healthCost", ability.array("health_costs").formatLevels())
            put("goldCost", ability.array("gold_costs").formatLevels())
            put("castRange", ability.array("cast_ranges").formatLevels())
            put("castPoint", ability.array("cast_points").formatLevels())
            put("channelTime", ability.array("channel_times").formatLevels())
            put("duration", ability.array("durations").formatLevels())
            put("params", values.mapNotNull { value -> parseParam(value) }.toJsonArray())
            if (hasFacets) {
                put("facets", ability.array("facets_loc").map { (it as String).resolve(values, heroValues, ValueSource.BASE) }.toJsonArray())
            }
        }
    }

    private fun parseParam(value: JSONObject): JSONObject? {
        val heading = value.string("heading_loc").trim().removeSuffix(":").trim()
        if (heading.isEmpty() || value.string("name") in headerValueNames) return null
        val isPercentage = value.bool("is_percentage")
        val base = value.array("values_float").formatLevels(isPercentage)
        val shard = value.array("values_shard").formatLevels(isPercentage)
        val scepter = value.array("values_scepter").formatLevels(isPercentage)
        val talent = value.array("bonuses").objects().firstOrNull()?.let { bonus ->
            val amount = (bonus["value"] as Number).toDouble()
            if (amount == 0.0) return@let null
            val sign = if (bonus.long("operation") == 0L && amount > 0) "+" else ""
            sign + amount.format() + if (isPercentage) "%" else ""
        }
        if (base == null && shard == null && scepter == null && talent == null) return null
        return JSONObject().apply {
            put("label", heading.cleanText())
            put("value", base)
            put("talent", talent)
            put("shard", shard)
            put("scepter", scepter)
        }
    }

    private fun parseFacet(facet: JSONObject): JSONObject = JSONObject().apply {
        put("name", facet.string("title_loc").ifEmpty { facet.string("name") })
        put("description", facet.string("description_loc").cleanText())
        put("color", facet["color"])
        put("icon", facet["icon"])
    }

    // ===== talents =====

    /**
     * Talents come in pairs from level 10 to 25. Inside a pair the first one is drawn on the right side of
     * the in-game tree, the second one on the left.
     */
    private fun parseTalents(talents: List<JSONObject>, abilities: List<JSONObject>): JSONArray {
        val texts = talents.map { talent -> resolveTalent(talent, abilities) }
        return texts.chunked(2).mapIndexed { index, pair ->
            JSONObject().apply {
                put("level", 10 + index * 5)
                put("right", pair.getOrNull(0) ?: "")
                put("left", pair.getOrNull(1) ?: "")
            }
        }.toJsonArray()
    }

    private val talentToken = Regex("""\{s:([A-Za-z0-9_]+)}""")

    private fun resolveTalent(talent: JSONObject, abilities: List<JSONObject>): String {
        val talentName = talent.string("name")
        val ownValues = talent.array("special_values").objects()
        return talent.string("name_loc").replace(talentToken) { match ->
            val token = match.groupValues[1]
            ownValues.firstOrNull { it.string("name") == token }?.let { own ->
                own.array("values_float").formatLevels(own.bool("is_percentage"))?.let { return@replace it }
            }
            val valueName = token.removePrefix("bonus_")
            val talentBonuses = abilities.asSequence()
                .flatMap { ability -> ability.array("special_values").objects() }
                .flatMap { value -> value.array("bonuses").objects().map { bonus -> value.string("name") to bonus } }
                .filter { (_, bonus) -> bonus.string("name") == talentName }
                .toList()
            // the token usually repeats the value name, but not always (bonus_armor_removed → armor_per_attack)
            val bonus = talentBonuses.firstOrNull { (name, _) -> name == valueName }?.second
                ?: talentBonuses.map { it.second }.distinctBy { it["value"] }.singleOrNull()
            bonus?.let { abs((it["value"] as Number).toDouble()).format() } ?: UNKNOWN_VALUE
        }.cleanText()
    }

    // ===== text =====

    private enum class ValueSource { BASE, SHARD, SCEPTER }

    private val valueToken = Regex("""%([A-Za-z_][A-Za-z0-9_]*)%""")

    /**
     * [values] are the ability's own values, [heroValues] are values of every hero ability: some descriptions
     * use a value of another ability. `%bonus_X%` is how much the upgrade changes X: in Shard/Scepter texts it is
     * values_shard/values_scepter of X, otherwise the talent bonus of X.
     * The datafeed misses a few values, they become [UNKNOWN_VALUE] instead of a raw placeholder.
     */
    private fun String.resolve(values: List<JSONObject>, heroValues: List<JSONObject>, source: ValueSource, unknown: String = UNKNOWN_VALUE): String = replace(valueToken) { match ->
        val token = match.groupValues[1]
        fun find(name: String) = values.firstOrNull { it.string("name").equals(name, ignoreCase = true) }
            ?: heroValues.firstOrNull { it.string("name").equals(name, ignoreCase = true) }
        val value = find(token) ?: run {
            val base = token.takeIf { it.startsWith("bonus_") }?.let { find(it.removePrefix("bonus_")) } ?: return@replace unknown
            val upgrade = when (source) {
                ValueSource.SHARD -> base.array("values_shard").formatLevels()
                ValueSource.SCEPTER -> base.array("values_scepter").formatLevels()
                ValueSource.BASE -> null
            }
            return@replace upgrade
                ?: base.array("bonuses").objects().firstOrNull()?.let { abs((it["value"] as Number).toDouble()).format() }
                ?: unknown
        }
        val levels = when (source) {
            ValueSource.SHARD -> value.array("values_shard").takeIf { it.isNotEmpty() }
            ValueSource.SCEPTER -> value.array("values_scepter").takeIf { it.isNotEmpty() }
            ValueSource.BASE -> null
        } ?: value.array("values_float").takeIf { it.isNotEmpty() }
        levels?.formatLevels()
            ?: value.array("bonuses").objects().firstOrNull()?.let { abs((it["value"] as Number).toDouble()).format() }
            ?: unknown
    }.replace("%%", "%").cleanText()

    private const val UNKNOWN_VALUE = "?"
    private const val MISSING = "\u0000"

    /**
     * Keeps only `<b>` and `<font color>` which the app renders, line breaks become `\n`.
     */
    internal fun String.cleanText(): String = this
        .replace("\r", "")
        .replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("""</?(?!b>|/b>|font\b|/font>)[a-zA-Z][^>]*>"""), "")
        .replace("&nbsp;", " ")
        .replace(Regex("""[ \t]+\n"""), "\n")
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()

    // ===== numbers =====

    private fun JSONArray.formatLevels(isPercentage: Boolean = false): String? {
        val numbers = map { (it as Number).toDouble() }
        if (numbers.isEmpty() || numbers.all { it == 0.0 }) return null
        val distinct = if (numbers.distinct().size == 1) listOf(numbers.first()) else numbers
        return distinct.joinToString("/") { it.format() + if (isPercentage) "%" else "" }
    }

    internal fun Double.format(): String {
        val rounded = round(2)
        return if (rounded == rounded.roundToLong().toDouble()) rounded.roundToLong().toString() else rounded.toString()
    }

    private fun Double.round(digits: Int): Double {
        var factor = 1.0
        repeat(digits) { factor *= 10 }
        return (this * factor).roundToLong() / factor
    }

    // ===== enums =====

    private fun targetTeam(value: Long) = when (value) {
        1L -> "allies"
        2L -> "enemies"
        3L -> "both"
        else -> null
    }

    private fun damageType(value: Long) = when (value) {
        1L -> "physical"
        2L -> "magical"
        4L -> "pure"
        else -> null
    }

    private fun spellImmunity(value: Long) = when (value) {
        1L, 3L -> "yes"
        2L, 4L -> "no"
        5L -> "allies_yes_enemies_no"
        else -> null
    }

    private fun dispellable(value: Long) = when (value) {
        1L -> "strong"
        2L -> "yes"
        3L -> "no"
        else -> null
    }

    // ===== json helpers =====

    private fun JSONObject.string(key: String): String = this[key]?.toString() ?: ""
    private fun JSONObject.long(key: String): Long = when (val value = this[key]) {
        is Number -> value.toLong()
        is String -> value.toLongOrNull() ?: value.toDoubleOrNull()?.toLong() ?: 0L
        else -> 0L
    }
    private fun JSONObject.double(key: String): Double = when (val value = this[key]) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
    private fun JSONObject.bool(key: String): Boolean = this[key]?.toString().equals("true", ignoreCase = true)
    private fun JSONObject.array(key: String): JSONArray = this[key] as? JSONArray ?: JSONArray()

    @Suppress("UNCHECKED_CAST")
    private fun JSONArray.objects(): List<JSONObject> = (this as List<Any?>).filterIsInstance<JSONObject>()

    @Suppress("UNCHECKED_CAST")
    private fun List<Any?>.toJsonArray(): JSONArray = JSONArray().also { array -> (array as MutableList<Any?>).addAll(this) }
}
