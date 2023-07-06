package n7.ad2.heroes.domain.internal

import n7.ad2.heroes.domain.UpdateStateViewedForHeroUseCase

internal class UpdateStateViewedForHeroUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : UpdateStateViewedForHeroUseCase {

    override fun invoke(name: String) {
        heroesRepository.updateViewedByUserFieldForName(name)
    }
}
