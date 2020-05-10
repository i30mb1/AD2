package n7.ad2.data.source.local

import android.app.Application
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.db.HeroesDao
import javax.inject.Inject

class Repository @Inject constructor(
        private val application: Application,
        private val appDatabase: AppDatabase
) {

    companion object {
        const val ASSETS_PATH_HEROES = "heroes.json"
        const val ASSETS_PATH_HERO_DESC = "description.json"
    }

    suspend fun getHeroesDao(): HeroesDao {
        return appDatabase.heroesDao
    }

    suspend fun getAssetsFile(filePath: String): String {
       return application.assets.open(filePath).bufferedReader().use {
            it.readText()
        }
    }

}