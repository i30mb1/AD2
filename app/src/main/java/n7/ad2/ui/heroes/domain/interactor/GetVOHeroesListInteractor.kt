package n7.ad2.ui.heroes.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import n7.ad2.AD2Logger
import n7.ad2.ui.heroes.domain.usecase.ConvertLocalHeroListToVoListUseCase
import n7.ad2.ui.heroes.domain.usecase.GetAllHeroesFromRepositoryUseCase
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class GetVOHeroesListInteractor @Inject constructor(
    private val convertLocalHeroListToVoListUseCase: ConvertLocalHeroListToVoListUseCase,
    private val getAllHeroesFromRepositoryUseCase: GetAllHeroesFromRepositoryUseCase,
    private val logger: AD2Logger,
) {

    operator fun invoke(): Flow<List<VOHero>> {
        return getAllHeroesFromRepositoryUseCase()
            .onStart { logger.log("get all heroes") }
            .map(convertLocalHeroListToVoListUseCase::invoke)
    }
}