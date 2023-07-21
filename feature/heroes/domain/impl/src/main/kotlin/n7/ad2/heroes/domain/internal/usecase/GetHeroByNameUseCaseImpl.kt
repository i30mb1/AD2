package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.first
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase

internal class GetHeroByNameUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroByNameUseCase {

    override suspend fun invoke(name: String): Hero {
        return heroesRepository.getHero(name).first()
    }
}
