package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import javax.inject.Inject

class FilterItemsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(list: List<VOItem>, filter: String): List<VOItem> = withContext(ioDispatcher) {
        list.map { it as VOItemBody }
            .filter { it.name.contains(filter, true) }
    }
}