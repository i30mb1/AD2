package n7.ad2.items.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.internal.domain.vo.VOItem
import javax.inject.Inject

internal class FilterItemsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(list: List<VOItem>, filter: String): List<VOItem> = withContext(dispatchers.IO) {
        list.map { it as VOItem.Body }
            .filter { it.name.contains(filter, true) }
    }
}