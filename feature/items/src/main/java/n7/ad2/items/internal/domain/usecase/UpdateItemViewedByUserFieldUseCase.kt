package n7.ad2.items.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ItemRepository
import javax.inject.Inject

internal class UpdateItemViewedByUserFieldUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.IO) {
        itemRepository.updateItemViewedByUserField(name)
    }
}