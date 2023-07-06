package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.UpdateStateViewedForHeroUseCase

internal class UpdateStateViewedForHeroUseCaseImpl(
    private val heroesRepository: HeroesRepository,
    private val dispatcher: DispatchersProvider,
) : UpdateStateViewedForHeroUseCase {

    override suspend fun invoke(name: String) = withContext(dispatcher.IO) {
        heroesRepository.updateViewedByUserFieldForName(name)
    }
}
