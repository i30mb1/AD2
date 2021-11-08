package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.AD2Logger
import n7.ad2.base.DispatchersProvider
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.ui.items.domain.vo.VOItemBody
import javax.inject.Inject

class GetVOItemsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: ItemRepository,
    private val logger: AD2Logger,
) {

    operator fun invoke(): Flow<List<VOItemBody>> {
        return itemRepository.getAllItems()
            .onStart { logger.log("items loaded") }
            .flatMapLatest { list ->
                flow {
                    emit(list.map { VOItemBody(it.name, ItemRepository.getFullUrlItemImage(it.name), it.viewedByUser) })
                }
            }
            .flowOn(dispatchers.IO)
    }
}