package n7.ad2.items.domian.internal.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domian.internal.ItemsRepository

internal class GetItemsUseCaseImpl(private val itemRepository: ItemsRepository) : GetItemsUseCase {

    override operator fun invoke(): Flow<List<Item>> = itemRepository.getAllItems()
}
