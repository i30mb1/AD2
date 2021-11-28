package n7.ad2.ui.splash.domain.interactor

import ad2.n7.logger.AD2Logger
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.splash.domain.model.AssetsHeroList
import javax.inject.Inject

class PopulateHeroesDatabaseInteractor @Inject constructor(
    private val moshi: Moshi,
    private val heroRepository: HeroRepository,
    private val logger: AD2Logger,
    private val dispatcher: ad2.n7.coroutines.DispatchersProvider,
) {

    class PopulateHeroesDatabaseException(message: String) : Exception(message)

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val json = heroRepository.getAssetsHeroes()
        if (json.isEmpty()) throw PopulateHeroesDatabaseException("File with heroes empty or not exist")

        val assetsHeroesList = moshi.adapter(AssetsHeroList::class.java).fromJson(json)?.heroes ?: throw PopulateHeroesDatabaseException("Could not parse assets heroes")

        val localHeroesList = assetsHeroesList.map { LocalHero(name = it.name, mainAttr = it.mainAttribute, viewedByUser = false) }

        heroRepository.insertHeroes(localHeroesList)
        logger.log("Hero loaded in DB")
        emit(true)
    }.flowOn(dispatcher.IO)

}