package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class SaveLocalHeroesInDatabaseUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(list: List<LocalHero>) = withContext(ioDispatcher) {
        heroRepository.insertHeroes(list)
    }

}