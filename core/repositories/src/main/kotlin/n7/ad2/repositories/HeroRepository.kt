@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides
import n7.ad2.repositories.model.LocalHeroDescription
import javax.inject.Inject

class HeroRepository @Inject constructor(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
) {

    companion object {
        const val ASSETS_FILE_ANIMATION = "emoticon.webp"
        fun getFullUrlHeroMinimap(heroName: String) = "file:///android_asset/heroes/$heroName/minimap.png"
        fun getFullUrlHeroImage(heroName: String) = "file:///android_asset/heroes/$heroName/full.png"
        fun getFullUrlHeroArcane(heroName: String) = "file:///android_asset/heroes/$heroName/arcane.webp"
        fun getFullUrlHeroSpell(spellName: String) = "file:///android_asset/images/${spellName.replace(" ", "_")}.webp"
    }

    suspend fun getSpellBitmap(spellName: String): Bitmap {
        return res.getAssets("spell/$spellName.webp").use { BitmapFactory.decodeStream(it) }
    }

    fun getAllHeroes(): Flow<List<LocalHero>> {
        return heroesDao.getAllHeroes()
    }

    fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }

    fun getHeroWithGuides(heroName: String): Flow<LocalHeroWithGuides> {
        return heroesDao.getHeroWithGuides(heroName)
    }

    suspend fun getHero(name: String): LocalHero {
        return heroesDao.getHero(name)
    }

    fun getHeroDescription(heroName: String, appLocale: AppLocale): Flow<LocalHeroDescription> {
        return flow {
            val json = res.getAssets("heroes/$heroName/${appLocale.value}/description.json")
                .bufferedReader()
                .use { it.readText() }
            val info = moshi.adapter(LocalHeroDescription::class.java).fromJson(json) ?: error("could not parse hero description")
            emit(info)
        }
    }

}