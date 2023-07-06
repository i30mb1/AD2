package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides
import n7.ad2.heroes.domain.internal.data.model.LocalHeroDescription
import java.io.InputStream

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<LocalHero>>
    fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuides>
    fun getHeroDescription(name: String): Flow<LocalHeroDescription>
    fun updateViewedByUserFieldForName(name: String)
    suspend fun getHero(name: String): LocalHero
    suspend fun getSpellInputStream(spellName: String): InputStream
}

fun getFullUrlHeroMinimap(heroName: String) = "file:///android_asset/heroes/$heroName/minimap.png"
fun getFullUrlHeroImage(heroName: String) = "file:///android_asset/heroes/$heroName/full.png"
fun getFullUrlHeroArcane(heroName: String) = "file:///android_asset/heroes/$heroName/arcane.webp"
fun getFullUrlHeroSpell(spellName: String) = "file:///android_asset/images/${spellName.replace(" ", "_")}.webp"
