package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ItemRepository
import javax.inject.Inject

class UpdateItemViewedByUserFieldUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val itemRepository: ItemRepository,
) {

    suspend operator fun invoke(name: String) = withContext(ioDispatcher) {
        itemRepository.updateItemViewedByUserField(name)
    }
}