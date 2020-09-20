package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ItemRepository
import javax.inject.Inject

class GetJsonItemsFromAssetsUseCase @Inject constructor(
    private val repository: ItemRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): String = withContext(ioDispatcher) {
        repository.getAssetsItems()
    }

}