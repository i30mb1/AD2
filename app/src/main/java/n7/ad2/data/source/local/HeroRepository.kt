@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.data.source.local

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.flow.Flow
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.data.source.local.model.LocalHeroWithGuides
import javax.inject.Inject

class HeroRepository @Inject constructor(
    private val application: Application,
    private val appDatabase: AppDatabase,
) {

    companion object {
        const val ASSETS_PATH_HEROES = "heroes.json"
        const val ASSETS_PATH_HERO_DESC = "description.json"
        const val ASSETS_FILE_FULL = "full.png"
        const val ASSETS_FILE_ANIMATION = "emoticon.webp"
        fun getFullUrlHeroMinimap(heroName: String) = "file:///android_asset/heroes/${heroName}/minimap.png"
        fun getFullUrlHeroImage(heroName: String) = "file:///android_asset/heroes/${heroName}/full.png"
        fun getFullUrlHeroSpell(spellName: String) = "file:///android_asset/heroesSpell/${spellName}.png"
    }

    suspend fun getHeroAnimation(assetsPath: String, name: String): Bitmap {
        return application.assets.open("$assetsPath/$name/$ASSETS_FILE_ANIMATION").use {
            BitmapFactory.decodeStream(it)
        }
    }

    fun updateViewedByUserFieldForName(name: String) {
        appDatabase.heroesDao.updateViewedByUserFieldForName(name)
    }

    fun getHeroWithGuides(heroName: String): Flow<LocalHeroWithGuides> {
        return appDatabase.heroesDao.getHeroWithGuides(heroName)
    }

    suspend fun getHero(name: String): LocalHero {
        return appDatabase.heroesDao.getHero(name)
    }

    suspend fun insertHeroes(list: List<LocalHero>) {
        appDatabase.heroesDao.insert(list)
    }

    fun getHeroDescription(assetsPath: String, locale: Locale): String {
        return application.assets.open("$assetsPath/${locale.folderName}/$ASSETS_PATH_HERO_DESC").bufferedReader().use {
            it.readText()
        }
    }

    fun getAssetsHeroes(): String {
        return application.assets.open(ASSETS_PATH_HEROES).bufferedReader().use {
            it.readText()
        }
    }

}