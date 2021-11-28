package n7.ad2.ui.heroGuide.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.GuideRepository
import n7.ad2.data.source.local.model.LocalGuide
import javax.inject.Inject

class SaveLocalGuideUseCase @Inject constructor(
    private val repository: GuideRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(localGuide: List<LocalGuide>) = withContext(dispatchers.Default) {
        repository.insertGuideAndDeleteOldGuides(localGuide)
    }

}