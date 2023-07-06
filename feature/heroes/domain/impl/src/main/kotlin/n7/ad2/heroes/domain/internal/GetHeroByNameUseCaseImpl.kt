package n7.ad2.heroes.domain.internal

import n7.ad2.heroes.domain.GetHeroByNameUseCase
import n7.ad2.heroes.domain.Hero
import n7.ad2.heroes.domain.internal.data.LocalHeroToHeroMapper

internal class GetHeroByNameUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroByNameUseCase {

    override suspend fun invoke(name: String): Hero {
        return LocalHeroToHeroMapper(heroesRepository.getHero(name))
    }
}
