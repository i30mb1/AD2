package n7.ad2.heroes.domain

import kotlinx.coroutines.flow.Flow

interface GetGuideForHeroUseCase {

    operator fun invoke(name: String): Flow<List<Guide>>
}
