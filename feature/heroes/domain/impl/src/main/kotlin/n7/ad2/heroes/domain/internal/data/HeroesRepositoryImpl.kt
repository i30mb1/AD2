package n7.ad2.heroes.domain.internal.data

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.heroes.domain.internal.HeroesRepository
import java.io.InputStream

internal class HeroesRepositoryImpl(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
) : HeroesRepository {

    override fun getAllHeroes(): Flow<List<LocalHero>> {
        return heroesDao.getAllHeroes()
    }

    override suspend fun getHero(name: String): LocalHero {
        return heroesDao.getHero(name)
    }

    override suspend fun getSpellInputStream(spellName: String): InputStream {
        return res.getAssets("spell/$spellName.webp")
    }

    override fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }
}
