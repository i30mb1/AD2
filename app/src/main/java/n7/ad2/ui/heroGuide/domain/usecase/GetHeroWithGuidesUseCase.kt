package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import javax.inject.Inject

class GetHeroWithGuidesUseCase @Inject constructor(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(heroName: String) = withContext(ioDispatcher) {
        repository.getHeroWithGuides(heroName)
    }

}