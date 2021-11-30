package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class UpdateItemViewedByUserFieldUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: n7.ad2.repositories.ItemRepository,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.IO) {
        itemRepository.updateItemViewedByUserField(name)
    }
}