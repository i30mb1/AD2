package n7.ad2.ui.heroResponse.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import java.io.File
import javax.inject.Inject

class GetSavedHeroResponseUseCase @Inject constructor(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(heroName: String): List<File> = withContext(ioDispatcher) {
        val savedHeroResponses = repository.getSavedHeroResponses(heroName)
        savedHeroResponses.toList()
    }

}