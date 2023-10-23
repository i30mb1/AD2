package n7.ad2.items.internal.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.ui.adapter.HeaderViewHolder

internal class GetItemsUIUseCase @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val logger: Logger,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(): Flow<List<ItemUI>> {
        return getItemsUseCase()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list: List<Item> ->
                val result = mutableListOf<ItemUI>()
                flow {
                    list.groupBy { item -> item.type }
                        .forEach { (type, items): Map.Entry<String, List<Item>> ->
                            result.add(ItemUI.Header(HeaderViewHolder.Data(type)))
                            result.addAll(items.map { item ->
                                ItemUI.Body(item.name, item.imageUrl, item.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }
            .flowOn(dispatchers.IO)
    }
}
