package n7.ad2.items.domian.internal.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.usecase.FilterItemsUseCase

internal class FilterItemsUseCaseImpl(private val dispatchers: DispatchersProvider) : FilterItemsUseCase {

    override suspend fun invoke(list: List<Item>, filter: String): List<Item> = withContext(dispatchers.IO) {
        list.filter { item ->
            item.toString().contains(filter, ignoreCase = true)
        }
    }
}
