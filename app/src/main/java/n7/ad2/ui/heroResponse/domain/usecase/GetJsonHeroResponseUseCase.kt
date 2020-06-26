package n7.ad2.ui.heroResponse.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import javax.inject.Inject

class GetJsonHeroResponseUseCase @Inject constructor(
        private val repository: Repository,
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(heroAssetsPath: String, locale: String) = withContext(ioDispatcher) {
        repository.getHeroResponses(heroAssetsPath, locale)
    }

}