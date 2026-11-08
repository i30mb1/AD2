package n7.ad2.heroes.domain.internal.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.heroes.domain.internal.data.db.HeroDatabase
import n7.ad2.heroes.domain.internal.data.db.HeroesDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Every use case of the heroes feature gets its own repository (the wiring is not a singleton), they share one database:
 * the menu and the heroes screen asked for heroes at once and Kez, Largo, Muerta and Ringmaster were added twice.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HeroesRepositoryTest {

    private val database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HeroesDatabase::class.java).build()

    private val list = """
        [{"name":"Axe","attribute":"strength","key":"npc_dota_hero_axe"},{"name":"Kez","attribute":"agility","key":"npc_dota_hero_kez"},
        {"name":"Largo","attribute":"strength","key":"npc_dota_hero_largo"}]
    """.trimIndent()

    private fun repository() = HeroesRepositoryImpl(database) { list }

    @After
    fun close() = database.close()

    @Test
    fun `heroes added twice before are left once`() = runBlocking {
        database.heroesDao.insertAll(
            listOf("Axe", "Kez", "Kez", "Largo", "Largo").map { name -> HeroDatabase(name = name, mainAttr = "strength", viewedByUser = false) },
        )

        val heroes = repository().getAllHeroes().first()

        assertEquals(listOf("Axe", "Kez", "Largo"), heroes.map { it.name })
    }

    @Test
    fun `repositories asking at once add a new hero once`() = runBlocking {
        database.heroesDao.insertAll(listOf(HeroDatabase(name = "Axe", mainAttr = "Strength", viewedByUser = false)))

        List(8) { repository() }.map { repository -> async(Dispatchers.Default) { repository.getAllHeroes().first() } }.awaitAll()

        assertEquals(listOf("Axe", "Kez", "Largo"), repository().getAllHeroes().first().map { it.name })
    }
}
