package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.base.adapter.HeaderViewHolder
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.logger.AD2Logger
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.ui.items.domain.vo.VOItemHeader
import javax.inject.Inject

class GetVOItemsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: n7.ad2.repositories.ItemRepository,
    private val logger: AD2Logger,
) {

    operator fun invoke(): Flow<List<VOItem>> {
        return itemRepository.getAllItems()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list ->
                val result = mutableListOf<VOItem>()
                flow {
                    list.groupBy { localItem -> localItem.type }
                        .forEach { map: Map.Entry<String, List<n7.ad2.database_guides.internal.model.LocalItem>> ->
                            result.add(VOItemHeader(HeaderViewHolder.Data(map.key)))
                            result.addAll(map.value.map { localItem ->
                                VOItemBody(localItem.name, n7.ad2.repositories.ItemRepository.getFullUrlItemImage(localItem.name), localItem.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }
            .flowOn(dispatchers.IO)
    }
}