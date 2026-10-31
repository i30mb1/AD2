package n7.ad2.hero.page.internal.info.domain.vo

import kotlin.math.floor
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroStats

/** Levels the "Info" page can show the stats for: the start, the middle and the end of a game. */
val STAT_LEVELS = listOf(1, 15, 25)

/**
 * Stats at [level] without items and talents, grown the way the game grows them: every level adds the attribute
 * gains, and each level with nothing left to learn adds [ATTRIBUTE_BONUS] to every attribute (6 of them by level 25).
 * Health, mana and damage count whole attribute points, regeneration, armor and magic resistance exact ones.
 * Checked against the level tables of the Dota 2 wiki.
 */
fun HeroDescription.statsAt(level: Int): HeroStats {
    if (level <= 1) return stats
    val bonus = ATTRIBUTE_BONUS * attributeBonusLevels.count { it <= level }
    val strength = attributes.strengthGain * (level - 1) + bonus
    val agility = attributes.agilityGain * (level - 1) + bonus
    val intelligence = attributes.intelligenceGain * (level - 1) + bonus
    val wholeStrength = whole(attributes.strength + strength) - attributes.strength
    val wholeAgility = whole(attributes.agility + agility) - attributes.agility
    val wholeIntelligence = whole(attributes.intelligence + intelligence) - attributes.intelligence
    val damage = when (primaryAttribute) {
        HeroAttribute.STRENGTH -> wholeStrength
        HeroAttribute.AGILITY -> wholeAgility
        HeroAttribute.INTELLIGENCE -> wholeIntelligence
        HeroAttribute.UNIVERSAL -> UNIVERSAL_DAMAGE_PER_POINT * (strength + agility + intelligence)
    }
    return stats.copy(
        health = stats.health + HEALTH_PER_STRENGTH * wholeStrength,
        healthRegen = stats.healthRegen + HEALTH_REGEN_PER_STRENGTH * strength,
        mana = stats.mana + MANA_PER_INTELLIGENCE * wholeIntelligence,
        manaRegen = stats.manaRegen + MANA_REGEN_PER_INTELLIGENCE * intelligence,
        armor = stats.armor + agility / AGILITY_PER_ARMOR,
        magicResist = stats.magicResist + MAGIC_RESIST_PER_INTELLIGENCE * intelligence,
        damageMin = stats.damageMin + damage,
        damageMax = stats.damageMax + damage,
    )
}

/** Whole points of an attribute, 47.99999 from adding gains is 48. */
private fun whole(points: Double): Double = floor(points + 1e-6)

/** Levels where a hero with every ability learned gets the attribute bonus instead. */
private val attributeBonusLevels = listOf(17, 19, 21, 22, 23, 24, 26)
private const val ATTRIBUTE_BONUS = 2

private const val HEALTH_PER_STRENGTH = 22
private const val HEALTH_REGEN_PER_STRENGTH = 0.1
private const val MANA_PER_INTELLIGENCE = 12
private const val MANA_REGEN_PER_INTELLIGENCE = 0.05
private const val MAGIC_RESIST_PER_INTELLIGENCE = 0.1
private const val AGILITY_PER_ARMOR = 6
private const val UNIVERSAL_DAMAGE_PER_POINT = 0.45
