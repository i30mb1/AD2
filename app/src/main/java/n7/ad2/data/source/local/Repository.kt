@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.data.source.local

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import kotlinx.coroutines.flow.Flow
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.data.source.local.model.LocalHeroWithGuides
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
        private val application: Application,
        private val appDatabase: AppDatabase
) {

    companion object {
        const val ASSETS_FOLDER_HEROES = "heroes"
        const val ASSETS_PATH_HEROES = "heroes.json"
        const val ASSETS_PATH_HERO_DESC = "description.json"
        const val ASSETS_PATH_HERO_RESPONSES = "responses.json"
        const val ASSETS_FILE_MINIMAP = "minimap.png"
        const val ASSETS_FILE_FULL = "full.png"
        const val ASSETS_FILE_ANIMATION = "emoticon.webp"
        val DIRECTORY_RESPONSES: String = Environment.DIRECTORY_RINGTONES
    }

    suspend fun getHeroAnimation(assetsPath: String, name: String): Bitmap {
        return application.assets.open("$assetsPath/$name/$ASSETS_FILE_ANIMATION").use {
            BitmapFactory.decodeStream(it)
        }
    }

    suspend fun updateViewedByUserFieldForName(name: String) {
        appDatabase.heroesDao.updateViewedByUserFieldForName(name)
    }

    suspend fun getHeroWithGuides(heroName: String): Flow<LocalHeroWithGuides> {
        return appDatabase.heroesDao.getHeroWithGuides(heroName)
    }

    suspend fun insertGuide(localGuide: LocalGuide): Long {
        return appDatabase.guideDao.insert(localGuide)
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

    suspend fun getSavedHeroResponses(heroName: String): Array<File> {
        return application.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.listFiles() ?: emptyArray()
    }

    suspend fun getAssetsHeroes(): String {
        return application.assets.open(ASSETS_PATH_HEROES).bufferedReader().use {
            it.readText()
        }
    }

}