package n7.ad2.heroes.domain.internal.data

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.model.LocalHeroDescription
import java.io.InputStream

internal class HeroesRepositoryImpl(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
    private val appLocale: AppLocale,
) : HeroesRepository {

    override fun getAllHeroes(): Flow<List<LocalHero>> {
        return heroesDao.getAllHeroes()
    }

    override fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuides> {
        return heroesDao.getHeroWithGuides(name)
    }

    override suspend fun getHero(name: String): LocalHero {
        return heroesDao.getHero(name)
    }

    override suspend fun getSpellInputStream(spellName: String): InputStream {
        return res.getAssets("spell/$spellName.webp")
    }

    override fun getHeroDescription(name: String): Flow<LocalHeroDescription> = flow {
        val json = res.getAssets("heroes/$name/${appLocale.value}/description.json")
            .bufferedReader()
            .use { it.readText() }
        moshi.adapter(LocalHeroDescription::class.java).fromJson(json) ?: error("could not parse hero($name) description")
    }

    override fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }
}
