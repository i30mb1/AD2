package n7.ad2.items.internal.domain.usecase

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
import n7.ad2.items.internal.domain.vo.VOItem
import n7.ad2.ui.adapter.HeaderViewHolder

internal class GetVOItemsUseCase @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val logger: Logger,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(): Flow<List<VOItem>> {
        return getItemsUseCase()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list: List<Item> ->
                val result = mutableListOf<VOItem>()
                flow {
                    list.groupBy { localItem -> localItem.type }
                        .forEach { map: Map.Entry<String, List<Item>> ->
                            result.add(VOItem.Header(HeaderViewHolder.Data(map.key)))
                            result.addAll(map.value.map { localItem ->
                                VOItem.Body(localItem.name, localItem.imageUrl, localItem.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }
            .flowOn(dispatchers.IO)
    }
}
