package n7.ad2.ui.heroResponse.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.ResponseRepository
import javax.inject.Inject

class GetJsonHeroResponseUseCase @Inject constructor(
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(heroName: String, locale: Locale) = withContext(dispatchers.Default) {
        repository.getHeroResponses(heroName, locale)
    }

}