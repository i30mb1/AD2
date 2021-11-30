package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class UpdateViewedByUserFieldUseCase @Inject constructor(
    private val heroRepository: n7.ad2.repositories.HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.Default) {
        heroRepository.updateViewedByUserFieldForName(name)
    }

}