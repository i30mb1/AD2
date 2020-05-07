package n7.ad2.data.source.local

import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.db.HeroesDao
import javax.inject.Inject

class Repository @Inject constructor(
        private val appDatabase: AppDatabase
) {

    suspend fun getHeroesDao(): HeroesDao {
        return appDatabase.heroesDao
    }

}