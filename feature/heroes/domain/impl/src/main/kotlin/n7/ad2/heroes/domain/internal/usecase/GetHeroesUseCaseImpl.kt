package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEmpty
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase

internal class GetHeroesUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroesUseCase {

    override fun invoke(): Flow<List<Hero>> {
        return heroesRepository.getAllHeroes()
            .onEmpty {

            }
    }
}
