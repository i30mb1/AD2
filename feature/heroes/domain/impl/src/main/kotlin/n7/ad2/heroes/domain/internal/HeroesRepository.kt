package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHero
import java.io.InputStream

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<LocalHero>>
    suspend fun getHero(name: String): LocalHero
    suspend fun getSpellInputStream(spellName: String): InputStream
}
