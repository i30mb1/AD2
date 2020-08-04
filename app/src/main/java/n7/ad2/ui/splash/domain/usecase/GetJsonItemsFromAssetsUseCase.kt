package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import javax.inject.Inject

class GetJsonItemsFromAssetsUseCase @Inject constructor(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): String = withContext(ioDispatcher) {
        repository.getAssetsFile(Repository.ASSETS_PATH_ITEMS)
    }

}