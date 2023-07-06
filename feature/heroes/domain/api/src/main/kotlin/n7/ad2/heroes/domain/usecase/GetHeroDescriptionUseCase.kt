package n7.ad2.heroes.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.model.HeroDescription

interface GetHeroDescriptionUseCase {

    operator fun invoke(name: String): Flow<HeroDescription>
}
