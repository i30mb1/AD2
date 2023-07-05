package n7.ad2.heroes.domain.internal.data

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.heroes.domain.Hero
import n7.ad2.heroes.domain.internal.HeroesRepository

internal class HeroesRepositoryImpl(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
) : HeroesRepository {

    override fun getAllHeroes(): Flow<List<LocalHero>> {
        return heroesDao.getAllHeroes()
    }
}
