package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.AD2Logger
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class GetAllHeroesFromRepositoryUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val repository: HeroRepository,
    private val logger: AD2Logger,
) {

    suspend operator fun invoke(): List<LocalHero> = withContext(ioDispatcher) {
        logger.log("get all heroes")
        repository.getAllHeroes()
    }

}