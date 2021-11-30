package n7.ad2.database_guides.internal.domain.usecase

import android.app.Application
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.domain.model.AssetsHero
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

class PopulateHeroesDatabaseUseCase @Inject constructor(
    private val application: Application,
//    private val heroesDao: HeroesDao,
    private val moshi: Moshi,
    private val logger: AD2Logger,
    private val dispatcher: DispatchersProvider,
) {

    class PopulateHeroesDatabaseException(message: String) : Exception(message)

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val json = application.assets.open("heroes.json").bufferedReader().use {
            it.readText()
        }
        if (json.isEmpty()) throw PopulateHeroesDatabaseException("File with heroes empty or not exist")

        val typeAssetsHero = Types.newParameterizedType(List::class.java, AssetsHero::class.java)
        val adapter: JsonAdapter<List<AssetsHero>> = moshi.adapter(typeAssetsHero)
        val listAssetsHero: List<AssetsHero> = adapter.fromJson(json) ?: throw PopulateHeroesDatabaseException("Could not parse assets heroes")

        val result: List<LocalHero> = listAssetsHero.map { assetsItem ->
            LocalHero(name = assetsItem.name, mainAttr = assetsItem.mainAttribute, viewedByUser = false)
        }

//        heroesDao.insert(result)
        logger.log("Heroes list loaded in DB")
        emit(true)
    }.flowOn(dispatcher.IO)

}