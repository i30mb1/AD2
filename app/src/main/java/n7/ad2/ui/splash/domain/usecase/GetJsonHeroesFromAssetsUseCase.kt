package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.HeroRepository
import javax.inject.Inject

class GetJsonHeroesFromAssetsUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): String = withContext(ioDispatcher) {
        heroRepository.getAssetsHeroes()
    }

}