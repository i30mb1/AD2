package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.HeroDescription

internal class GetHeroDescriptionUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroDescriptionUseCase {

    override fun invoke(name: String): Flow<HeroDescription> {
        return heroesRepository.getHeroDescription(name)
            .map { HeroDescription() }
    }
}
