package n7.ad2.data.source.local

import android.app.Application
import android.content.SharedPreferences
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.db.HeroesDao
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class Repository @Inject constructor(
        private val application: Application,
        private val appDatabase: AppDatabase,
        private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val ASSETS_PATH_HEROES = "heroes.json"
        const val ASSETS_PATH_HERO_DESC = "description.json"
    }

    suspend fun insertHeroes(list: List<LocalHero>) {
        appDatabase.heroesDao.insert(list)
    }

    suspend fun getAssetsFile(filePath: String): String {
       return application.assets.open(filePath).bufferedReader().use {
            it.readText()
        }
    }

    suspend fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

}