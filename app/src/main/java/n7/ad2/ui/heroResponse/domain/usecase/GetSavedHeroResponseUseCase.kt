package n7.ad2.ui.heroResponse.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ResponseRepository
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