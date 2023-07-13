package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHeroWithGuidesDb
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.model.HeroDescription
import java.io.InputStream

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<Hero>>
    fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuidesDb>
    fun getHeroDescription(name: String): Flow<HeroDescription>
    fun updateViewedByUserFieldForName(name: String)
    suspend fun getHero(name: String): Flow<Hero>
    suspend fun getSpellInputStream(spellName: String): InputStream
}

fun getFullUrlHeroMinimap(heroName: String) = "file:///android_asset/heroes/$heroName/minimap.png"
fun getFullUrlHeroImage(heroName: String) = "file:///android_asset/heroes/$heroName/full.png"
fun getFullUrlHeroArcane(heroName: String) = "file:///android_asset/heroes/$heroName/arcane.webp"
fun getFullUrlHeroSpell(spellName: String) = "file:///android_asset/images/${spellName.replace(" ", "_")}.webp"
