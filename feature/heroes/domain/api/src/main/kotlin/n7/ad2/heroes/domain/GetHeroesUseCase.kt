package n7.ad2.heroes.domain

import kotlinx.coroutines.flow.Flow

interface GetHeroesUseCase {

    operator fun invoke(): Flow<List<Hero>>
}