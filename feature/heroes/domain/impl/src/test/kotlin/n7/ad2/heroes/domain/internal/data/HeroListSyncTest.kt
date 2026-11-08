package n7.ad2.heroes.domain.internal.data

import n7.ad2.heroes.domain.internal.data.db.HeroDatabase
import org.junit.Assert.assertEquals
import org.junit.Test

/** The prepackaged heroes.db is from 2023: no Muerta, Ringmaster, Kez, Largo and no universal heroes at all. */
class HeroListSyncTest {

    private val listJson = """
        [{"name":"Abaddon","attribute":"universal","key":"npc_dota_hero_abaddon"},{"name":"Axe","attribute":"strength","key":"npc_dota_hero_axe"},
        {"name":"Kez","attribute":"agility","key":"npc_dota_hero_kez"},{"name":"Ogre Magi","attribute":"strength","key":"npc_dota_hero_ogre_magi"}]
    """.trimIndent()

    @Test
    fun `the list of core parser is read`() {
        assertEquals(
            listOf(
                ListedHero("Abaddon", "universal", "npc_dota_hero_abaddon"),
                ListedHero("Axe", "strength", "npc_dota_hero_axe"),
                ListedHero("Kez", "agility", "npc_dota_hero_kez"),
                ListedHero("Ogre Magi", "strength", "npc_dota_hero_ogre_magi"),
            ),
            parseHeroList(listJson),
        )
    }

    @Test
    fun `new heroes are added, heroes that moved get their attribute, the rest stays as is`() {
        val known = mapOf("Abaddon" to "Strength", "Axe" to "Strength", "Ogre Magi" to "Intelligence")

        val changes = heroListChanges(known, parseHeroList(listJson))

        assertEquals(listOf("Kez"), changes.added.map { it.name })
        assertEquals(listOf("Abaddon" to "universal", "Ogre Magi" to "strength"), changes.moved.map { it.name to it.attribute })
    }

    @Test
    fun `heroes show the art of the hero page`() {
        val hero = HeroDatabaseToHeroMapper(HeroDatabase(name = "Nature's Prophet", mainAttr = "universal", viewedByUser = false))
        assertEquals("file:///android_asset/heroes/nature's_prophet/full.webp", hero.avatarUrl)
    }
}
