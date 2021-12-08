package n7.ad2.hero_page.internal.responses.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.android.Locale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ResponseRepository
import javax.inject.Inject

class GetJsonHeroResponseUseCase @Inject constructor(
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(heroName: String, locale: Locale) = withContext(dispatchers.Default) {
        repository.getHeroResponses(heroName, locale)
    }

}