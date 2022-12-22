package n7.ad2.heropage.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class SaveLastDayHeroGuidesLoadedUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(key: String, day: Int) = withContext(dispatchers.IO) {

    }
}