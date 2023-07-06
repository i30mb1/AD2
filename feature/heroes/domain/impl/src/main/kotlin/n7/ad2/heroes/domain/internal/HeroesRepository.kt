package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHero

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<LocalHero>>
    suspend fun getHero(name: String): LocalHero
}
