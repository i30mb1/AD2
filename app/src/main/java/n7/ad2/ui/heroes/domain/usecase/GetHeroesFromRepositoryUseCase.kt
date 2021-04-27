package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class GetHeroesFromRepositoryUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val repository: HeroRepository,
) {

    suspend operator fun invoke(filter: String): Flow<List<LocalHero>> = withContext(ioDispatcher) {
        repository.getHeroes(filter)
    }
}