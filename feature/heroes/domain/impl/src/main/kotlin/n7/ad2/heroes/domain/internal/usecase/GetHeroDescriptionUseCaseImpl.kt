package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase

internal class GetHeroDescriptionUseCaseImpl(private val heroesRepository: HeroesRepository) : GetHeroDescriptionUseCase {

    override fun invoke(name: String): Flow<HeroDescription> {
        TODO("GetHeroDescriptionUseCase not implemented")
    }
}
