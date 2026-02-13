package n7.ad2.items.domian.internal.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.items.domian.internal.ItemsRepository

internal class UpdateItemViewedForItemUseCaseImpl(private val itemRepository: ItemsRepository, private val dispatchers: DispatchersProvider) : UpdateItemViewedForItemUseCase {

    override suspend operator fun invoke(name: String) = withContext(dispatchers.IO) {
        itemRepository.updateItemViewedForUser(name)
    }
}
