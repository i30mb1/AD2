package n7.ad2.hero_page.internal.responses.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ResponseRepository
import javax.inject.Inject

class GetJsonHeroResponseUseCase @Inject constructor(
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(heroName: String, appLocale: AppLocale) = withContext(dispatchers.Default) {
        repository.getHeroResponses(heroName, appLocale)
    }

}