package n7.ad2.heroes.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.model.Hero

interface GetHeroesUseCase {

    operator fun invoke(): Flow<List<Hero>>
}