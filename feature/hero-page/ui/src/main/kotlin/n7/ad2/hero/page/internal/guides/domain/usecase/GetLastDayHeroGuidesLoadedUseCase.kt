package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class GetLastDayHeroGuidesLoadedUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(key: String): Int = withContext(dispatchers.Default) {
        0
    }
}
