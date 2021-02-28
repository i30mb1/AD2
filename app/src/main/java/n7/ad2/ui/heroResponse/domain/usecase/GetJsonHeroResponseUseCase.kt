package n7.ad2.ui.heroResponse.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.ResponseRepository
import javax.inject.Inject

class GetJsonHeroResponseUseCase @Inject constructor(
    private val repository: ResponseRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(heroName: String, locale: Locale) = withContext(ioDispatcher) {
        repository.getHeroResponses(heroName, locale)
    }

}