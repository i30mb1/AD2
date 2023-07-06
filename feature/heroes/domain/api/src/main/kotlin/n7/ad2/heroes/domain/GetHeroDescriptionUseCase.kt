package n7.ad2.heroes.domain

import kotlinx.coroutines.flow.Flow

interface GetHeroDescriptionUseCase {

    operator fun invoke(name: String): Flow<HeroDescription>
}
