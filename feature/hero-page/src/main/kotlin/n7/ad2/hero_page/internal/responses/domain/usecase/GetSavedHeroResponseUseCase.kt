package n7.ad2.hero_page.internal.responses.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ResponseRepository
import java.io.File
import javax.inject.Inject

class GetSavedHeroResponseUseCase @Inject constructor(
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(heroName: String): List<File> = withContext(dispatchers.Default) {
        val savedHeroResponses = repository.getSavedHeroResponses(heroName)
        savedHeroResponses.toList()
    }

}