package n7.ad2.heroes.domain.internal.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.db.dao.HeroesDao
import n7.ad2.heroes.domain.model.Hero

internal class HeroesRepositoryImpl(private val heroesDao: HeroesDao) : HeroesRepository {

    override fun getAllHeroes(): Flow<List<Hero>> = heroesDao.getAllHeroes()
        .map { localHeroList -> localHeroList.map(HeroDatabaseToHeroMapper) }

    override fun getHeroByName(): Hero {
        TODO("Not yet implemented")
    }

    override fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }
}
