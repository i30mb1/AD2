package n7.ad2.heroes.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.HeroRepository
import javax.inject.Inject

internal class UpdateViewedByUserFieldUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.Default) {
        heroRepository.updateViewedByUserFieldForName(name)
    }

}