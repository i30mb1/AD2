package n7.ad2.heroes.domain.internal.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.db.HeroDatabase
import n7.ad2.heroes.domain.internal.data.db.HeroesDatabase
import n7.ad2.heroes.domain.model.Hero

/** [readHeroList] gives `heroes/list.json` of the assets. */
internal class HeroesRepositoryImpl(private val database: HeroesDatabase, private val readHeroList: () -> String) : HeroesRepository {

    private val heroesDao = database.heroesDao

    override fun getAllHeroes(): Flow<List<Hero>> = flow {
        syncWithAssets()
        emitAll(heroesDao.getAllHeroes().map { localHeroList -> localHeroList.map(HeroDatabaseToHeroMapper) })
    }

    override fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }

    /**
     * The prepackaged heroes.db is from 2023, `heroes/list.json` (core/parser) has every hero the app has data for
     * with the attribute it has now. Existing rows keep their `viewedByUser` flag.
     * Every use case gets its own repository and the menu, the heroes screen and the games ask for heroes at once:
     * the sync is one transaction, Room runs the transactions of the database one after another, so a new hero is added
     * once. Heroes added twice by the first builds with the sync (2026-11-08) are left once.
     */
    private suspend fun syncWithAssets() {
        val listed = runCatching { parseHeroList(readHeroList()) }.getOrNull() ?: return
        database.withTransaction {
            heroesDao.deleteDuplicates()
            val changes = heroListChanges(heroesDao.getAllAttributes().associate { it.name to it.mainAttr }, listed)
            if (changes.added.isNotEmpty()) {
                heroesDao.insertAll(changes.added.map { HeroDatabase(name = it.name, mainAttr = it.attribute, viewedByUser = false) })
            }
            changes.moved.forEach { heroesDao.updateAttribute(it.name, it.attribute) }
        }
    }
}
