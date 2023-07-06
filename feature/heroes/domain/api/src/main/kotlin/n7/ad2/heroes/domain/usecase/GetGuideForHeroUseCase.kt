package n7.ad2.heroes.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.model.Guide

interface GetGuideForHeroUseCase {

    operator fun invoke(name: String): Flow<List<Guide>>
}
