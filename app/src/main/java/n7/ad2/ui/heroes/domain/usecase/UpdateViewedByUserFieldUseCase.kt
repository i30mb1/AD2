package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.HeroRepository
import javax.inject.Inject

class UpdateViewedByUserFieldUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(name: String) = withContext(ioDispatcher) {
        heroRepository.updateViewedByUserFieldForName(name)
    }

}