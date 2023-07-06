package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.LocalHeroToHeroMapper
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase

internal class GetHeroesUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroesUseCase {

    override fun invoke(): Flow<List<Hero>> {
        return heroesRepository.getAllHeroes()
            .map { localHeroList ->
                localHeroList.map(LocalHeroToHeroMapper)
            }
    }
}
