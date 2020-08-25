package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.model.LocalGuide
import javax.inject.Inject

class SaveLocalGuideUseCase @Inject constructor(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(localGuide: LocalGuide) = withContext(ioDispatcher) {
        repository.insertGuide(localGuide)
    }

}