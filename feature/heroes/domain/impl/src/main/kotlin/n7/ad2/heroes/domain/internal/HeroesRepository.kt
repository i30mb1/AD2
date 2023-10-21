package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.model.Hero

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<Hero>>
    fun updateViewedByUserFieldForName(name: String)
}

fun getFullUrlHeroMinimap(heroName: String) = "file:///android_asset/heroes/$heroName/minimap.png"
fun getFullUrlHeroArcane(heroName: String) = "file:///android_asset/heroes/$heroName/arcane.webp"
fun getFullUrlHeroSpell(spellName: String) = "file:///android_asset/images/${spellName.replace(" ", "_")}.webp"
