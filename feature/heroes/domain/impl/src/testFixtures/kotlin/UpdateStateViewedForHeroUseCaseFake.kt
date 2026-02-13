import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase

/**
 * @author e.shuvagin
 */
class UpdateStateViewedForHeroUseCaseFake : UpdateStateViewedForHeroUseCase {
    override suspend fun invoke(name: String) = Unit
}
