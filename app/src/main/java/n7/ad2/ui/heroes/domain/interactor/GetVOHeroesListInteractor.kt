package n7.ad2.ui.heroes.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.ui.heroes.domain.usecase.ConvertLocalHeroListToVoListUseCase
import n7.ad2.ui.heroes.domain.usecase.GetAllHeroesFromRepositoryUseCase
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class GetVOHeroesListInteractor @Inject constructor(
    private val convertLocalHeroListToVoListUseCase: ConvertLocalHeroListToVoListUseCase,
    private val getAllHeroesFromRepositoryUseCase: GetAllHeroesFromRepositoryUseCase,
) {

    operator fun invoke(): Flow<List<VOHero>> {
        return getAllHeroesFromRepositoryUseCase()
            .map(convertLocalHeroListToVoListUseCase::invoke)
    }
}