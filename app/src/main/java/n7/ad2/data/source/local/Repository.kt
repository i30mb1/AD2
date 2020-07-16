@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.data.source.local

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalHero
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
        private val application: Application,
        private val appDatabase: AppDatabase,
        private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val ASSETS_FOLDER_HEROES = "heroes"
        const val ASSETS_PATH_HEROES = "heroes.json"
        const val ASSETS_PATH_HERO_DESC = "description.json"
        const val ASSETS_PATH_HERO_RESPONSES = "responses.json"
        const val ASSETS_FILE_MINIMAP = "minimap.png"
        const val ASSETS_FILE_ANIMATION = "emoticon.webp"
        val DIRECTORY_RESPONSES = Environment.DIRECTORY_RINGTONES
    }

    suspend fun getHeroAnimation(assetsPath: String, name: String): Bitmap {
        return application.assets.open("$assetsPath/$name/$ASSETS_FILE_ANIMATION").use {
            BitmapFactory.decodeStream(it)
        }
    }


    suspend fun getHero(name: String): LocalHero {
        return appDatabase.heroesDao.getHero(name)
    }

    suspend fun insertHeroes(list: List<LocalHero>) {
        appDatabase.heroesDao.insert(list)
    }

    suspend fun getHeroDescription(assetsPath: String, locale: String): String {
        return application.assets.open("$assetsPath/$locale/$ASSETS_PATH_HERO_DESC").bufferedReader().use {
            it.readText()
        }
    }

    suspend fun getHeroResponses(assetsPath: String, locale: String): String {
        return application.assets.open("$assetsPath/$locale/$ASSETS_PATH_HERO_RESPONSES").bufferedReader().use {
            it.readText()
        }
    }

    suspend fun getSavedHeroResponses(heroName: String): Array<String>? {
        return application.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.list()
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