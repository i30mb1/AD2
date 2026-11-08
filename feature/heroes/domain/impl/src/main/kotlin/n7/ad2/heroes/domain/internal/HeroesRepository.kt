package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.model.Hero

internal interface HeroesRepository {

    fun getAllHeroes(): Flow<List<Hero>>
    fun updateViewedByUserFieldForName(name: String)
}
