package n7.ad2.items.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.internal.domain.vo.VOItem
import n7.ad2.logger.Logger
import n7.ad2.repositories.ItemRepository
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

internal class GetVOItemsUseCase @Inject constructor(
    private val logger: Logger,
    private val itemRepository: ItemRepository,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(): Flow<List<VOItem>> {
        return itemRepository.getAllItems()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list ->
                val result = mutableListOf<VOItem>()
                flow {
                    list.groupBy { localItem -> localItem.type }
                        .forEach { map: Map.Entry<String, List<n7.ad2.database_guides.internal.model.LocalItem>> ->
                            result.add(VOItem.Header(HeaderViewHolder.Data(map.key)))
                            result.addAll(map.value.map { localItem ->
                                VOItem.Body(localItem.name, ItemRepository.getFullUrlItemImage(localItem.name), localItem.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }
            .flowOn(dispatchers.IO)
    }
}