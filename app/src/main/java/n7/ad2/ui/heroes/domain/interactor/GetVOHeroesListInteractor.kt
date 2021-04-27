package n7.ad2.ui.heroes.domain.interactor

import n7.ad2.ui.heroes.domain.usecase.ConvertLocalHeroListToVoListUseCase
import n7.ad2.ui.heroes.domain.usecase.GetAllHeroesFromRepositoryUseCase
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class GetVOHeroesListInteractor @Inject constructor(
    private val convertLocalHeroListToVoListUseCase: ConvertLocalHeroListToVoListUseCase,
    private val getAllHeroesFromRepositoryUseCase: GetAllHeroesFromRepositoryUseCase,
) {

    suspend operator fun invoke(): List<VOHero> {
        val localHeroList = getAllHeroesFromRepositoryUseCase()
        return convertLocalHeroListToVoListUseCase(localHeroList)
    }
}