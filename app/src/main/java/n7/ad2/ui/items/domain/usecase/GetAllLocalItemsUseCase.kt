package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import n7.ad2.AD2Logger
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class GetAllLocalItemsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val itemRepository: ItemRepository,
    private val logger: AD2Logger,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    operator fun invoke(): Flow<List<LocalItem>> = itemRepository.getAllItems()
        .onEach { logger.log("get all items") }
        .flowOn(ioDispatcher)

}