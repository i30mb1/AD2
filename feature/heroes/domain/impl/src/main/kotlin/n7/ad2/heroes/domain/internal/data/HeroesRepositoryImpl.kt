package n7.ad2.heroes.domain.internal.data

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.db.HeroDatabase
import n7.ad2.heroes.domain.internal.data.db.dao.HeroesDao
import n7.ad2.heroes.domain.model.Hero

internal class HeroesRepositoryImpl(private val heroesDao: HeroesDao, private val application: Application) : HeroesRepository {

    // the heroes screen, the menu and the games ask for heroes at the same time, the list is synced once
    private val syncMutex = Mutex()
    private var isSynced = false

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
     */
    private suspend fun syncWithAssets() = syncMutex.withLock {
        if (isSynced) return@withLock
        runCatching {
            val listed = parseHeroList(application.assets.open("heroes/list.json").bufferedReader().use { it.readText() })
            val changes = heroListChanges(heroesDao.getAllAttributes().associate { it.name to it.mainAttr }, listed)
            if (changes.added.isNotEmpty()) {
                heroesDao.insertAll(changes.added.map { HeroDatabase(name = it.name, mainAttr = it.attribute, viewedByUser = false) })
            }
            changes.moved.forEach { heroesDao.updateAttribute(it.name, it.attribute) }
        }
        isSynced = true
    }
}
