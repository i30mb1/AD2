package n7.ad2.items.domian.internal.usecase

import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.usecase.FilterItemsUseCase

internal class FilterItemsUseCaseImpl(
    private val dispatchers: DispatchersProvider,
) : FilterItemsUseCase {

    override suspend fun invoke(list: List<Item>, filter: String): List<Item> {
        TODO("Not yet implemented")
    }
//    override suspend operator fun invoke(list: List<Item>, filter: String): List<VOItem> = withContext(dispatchers.IO) {
//        list.map { it as VOItem.Body }
//            .filter { it.name.contains(filter, true) }
//    }
}
