package n7.ad2.database_guides.internal.domain.usecase

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.internal.domain.model.AssetsHero
import n7.ad2.database_guides.internal.model.HeroDb
import javax.inject.Inject

class PopulateHeroesDatabaseUseCase @Inject constructor(
    private val res: Resources,
    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
    private val logger: Logger,
    private val dispatcher: DispatchersProvider,
) {

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val json = res.getAssets("heroes.json").bufferedReader().use { it.readText() }
        if (json.isEmpty()) error("File with heroes empty or not exist")

        val typeAssetsHero = Types.newParameterizedType(List::class.java, AssetsHero::class.java)
        val adapter: JsonAdapter<List<AssetsHero>> = moshi.adapter(typeAssetsHero)
        val listAssetsHero: List<AssetsHero> = adapter.fromJson(json) ?: error("Could not parse assets heroes")

        val result: List<HeroDb> = listAssetsHero.map { assetsItem ->
            HeroDb(name = assetsItem.name, mainAttr = assetsItem.mainAttribute, viewedByUser = false)
        }

        heroesDao.insert(result)
        logger.log("db filled with heroes")
        emit(true)
    }.flowOn(dispatcher.IO)

}
