package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroAttributes
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroStats
import org.junit.Assert.assertEquals
import org.junit.Test

class HeroLevelStatsTest {

    // Juggernaut from the Valve datafeed, level 1 values as the parser writes them
    private val juggernaut = HeroDescription(
        primaryAttribute = HeroAttribute.AGILITY,
        attributes = HeroAttributes(strength = 20.0, strengthGain = 2.0, agility = 32.0, agilityGain = 2.8, intelligence = 14.0, intelligenceGain = 1.4),
        stats = HeroStats(
            health = 560.0,
            healthRegen = 2.5,
            mana = 243.0,
            manaRegen = 0.7,
            armor = 5.3,
            magicResist = 26.4,
            damageMin = 54.0,
            damageMax = 56.0,
            attackRate = 1.4,
            attackRange = 150.0,
            moveSpeed = 305.0,
        ),
    )

    @Test
    fun `level 1 is what the datafeed gives`() {
        assertEquals(juggernaut.stats, juggernaut.statsAt(1))
    }

    @Test
    fun `level 15 adds 14 levels of attribute gain`() {
        val stats = juggernaut.statsAt(15)
        // strength 20 + 2 * 14 = 48, intelligence 14 + 1.4 * 14 = 33.6, agility 32 + 2.8 * 14 = 71.2
        assertEquals(1176.0, stats.health, DELTA)
        assertEquals(5.3, stats.healthRegen, DELTA)
        // health, mana and damage count whole points only: 33 intelligence, 71 agility
        assertEquals(471.0, stats.mana, DELTA)
        assertEquals(1.68, stats.manaRegen, DELTA)
        assertEquals(11.83, stats.armor, 0.01)
        assertEquals(28.36, stats.magicResist, DELTA)
        assertEquals(93.0, stats.damageMin, DELTA)
        assertEquals(95.0, stats.damageMax, DELTA)
    }

    @Test
    fun `level 25 also has 6 levels of the attribute bonus`() {
        val stats = juggernaut.statsAt(25)
        // +2 to every attribute for each of the 6 levels: strength 80, intelligence 59.6, agility 111.2
        assertEquals(1880.0, stats.health, DELTA)
        assertEquals(8.5, stats.healthRegen, DELTA)
        // the Dota 2 wiki table shows the same: 783 mana, 2.98 regeneration, 30.96% magic resistance
        assertEquals(783.0, stats.mana, DELTA)
        assertEquals(2.98, stats.manaRegen, DELTA)
        assertEquals(18.5, stats.armor, DELTA)
        assertEquals(30.96, stats.magicResist, DELTA)
        assertEquals(133.0, stats.damageMin, DELTA)
        assertEquals(135.0, stats.damageMax, DELTA)
    }

    @Test
    fun `stats the attributes do not touch stay the same`() {
        val stats = juggernaut.statsAt(25)
        assertEquals(1.4, stats.attackRate, DELTA)
        assertEquals(150.0, stats.attackRange, DELTA)
        assertEquals(305.0, stats.moveSpeed, DELTA)
    }

    @Test
    fun `a universal hero gets damage from every attribute`() {
        val abaddon = HeroDescription(
            primaryAttribute = HeroAttribute.UNIVERSAL,
            attributes = HeroAttributes(strength = 21.0, strengthGain = 2.6, agility = 22.0, agilityGain = 1.5, intelligence = 19.0, intelligenceGain = 2.0),
            stats = HeroStats(damageMin = 49.9, damageMax = 59.9),
        )
        // (2.6 + 1.5 + 2.0) * 24 + 3 * 12 = 182.4 attribute points, 0.45 damage each
        assertEquals(131.98, abaddon.statsAt(25).damageMin, DELTA)
        assertEquals(141.98, abaddon.statsAt(25).damageMax, DELTA)
    }

    private companion object {
        const val DELTA = 0.001
    }
}
