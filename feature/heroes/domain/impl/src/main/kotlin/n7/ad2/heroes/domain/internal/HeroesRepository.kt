package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides
import java.io.InputStream

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<LocalHero>>
    fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuides>
    fun updateViewedByUserFieldForName(name: String)
    suspend fun getHero(name: String): LocalHero
    suspend fun getSpellInputStream(spellName: String): InputStream
}
