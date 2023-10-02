package n7.ad2.items.internal.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ItemRepository

internal class UpdateItemViewedByUserFieldUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String) = withContext(dispatchers.IO) {
        itemRepository.updateItemViewedByUserField(name)
    }
}
