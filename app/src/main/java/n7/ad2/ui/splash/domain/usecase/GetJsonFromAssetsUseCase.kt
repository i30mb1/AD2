package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import javax.inject.Inject


@Suppress("BlockingMethodInNonBlockingContext")
class GetJsonFromAssetsUseCase @Inject constructor(
        private val repository: Repository,
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(filePath: String): String = withContext(ioDispatcher) {
        repository.getAssetsFile(filePath)
    }

}