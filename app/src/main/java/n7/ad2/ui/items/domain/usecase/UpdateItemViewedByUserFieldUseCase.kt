package n7.ad2.ui.items.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ItemRepository
import javax.inject.Inject

class UpdateItemViewedByUserFieldUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: ItemRepository,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.IO) {
        itemRepository.updateItemViewedByUserField(name)
    }
}