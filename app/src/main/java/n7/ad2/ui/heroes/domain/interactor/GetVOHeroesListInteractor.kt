package n7.ad2.ui.heroes.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.AD2Logger
import n7.ad2.base.DispatchersProvider
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroes.domain.vo.VOHero
import n7.ad2.ui.heroes.domain.vo.VOHeroBody
import javax.inject.Inject

class GetVOHeroesListInteractor @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val heroRepository: HeroRepository,
    private val logger: AD2Logger,
) {

    operator fun invoke(): Flow<List<VOHero>> {
        return heroRepository.getAllHeroes()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list ->
                val result = mutableListOf<VOHero>()
                flow {
                    list.forEach { localHero: LocalHero ->
                        result.add(
                            VOHeroBody(
                                localHero.name,
                                HeroRepository.getFullUrlHeroImage(localHero.name),
                                localHero.viewedByUser,
                            )
                        )
                    }
                    emit(result)
                }
            }.flowOn(dispatchers.IO)
    }

}