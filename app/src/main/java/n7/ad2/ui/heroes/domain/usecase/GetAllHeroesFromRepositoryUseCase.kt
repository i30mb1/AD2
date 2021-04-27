package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class GetAllHeroesFromRepositoryUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val repository: HeroRepository,
) {

    operator fun invoke(): Flow<List<LocalHero>> = repository.getAllHeroes().flowOn(ioDispatcher)

}