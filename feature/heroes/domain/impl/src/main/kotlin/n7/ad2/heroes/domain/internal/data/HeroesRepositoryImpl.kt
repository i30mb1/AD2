package n7.ad2.heroes.domain.internal.data

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.model.LocalHeroWithGuidesDb
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.model.HeroDescriptionJson
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.model.HeroDescription
import java.io.InputStream

internal class HeroesRepositoryImpl(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
    private val appLocale: AppLocale,
) : HeroesRepository {

    override fun getAllHeroes(): Flow<List<Hero>> {
        return heroesDao.getAllHeroes()
            .map { localHeroList -> localHeroList.map(LocalHeroToHeroMapper) }
    }

    override fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuidesDb> {
        return heroesDao.getHeroWithGuides(name)
    }

    override suspend fun getHero(name: String): Flow<Hero> {
        return heroesDao.getHero(name)
            .map(LocalHeroToHeroMapper)
    }

    override suspend fun getSpellInputStream(spellName: String): InputStream {
        return res.getAssets("spell/$spellName.webp")
    }

    override fun getHeroDescription(name: String): Flow<HeroDescription> = flow {
        val json = res.getAssets("heroes/$name/${appLocale.value}/description.json")
            .bufferedReader()
            .use { it.readText() }
        val heroJson = moshi.adapter(HeroDescriptionJson::class.java).fromJson(json) ?: error("could not parse hero($name) description")
        emit(HeroDescription())
    }

    override fun updateViewedByUserFieldForName(name: String) {
        heroesDao.updateViewedByUserFieldForName(name)
    }
}
