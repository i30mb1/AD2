package n7.ad2.items.internal.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.internal.model.ItemUI
import javax.inject.Inject

internal class FilterItemsUIUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    /**
     * Оставляет предметы, чьё название содержит [filter], и заголовки только тех групп, в которых что-то нашлось
     */
    suspend operator fun invoke(list: List<ItemUI>, filter: String): List<ItemUI> = withContext(dispatchers.IO) {
        if (filter.isBlank()) return@withContext list
        val query = filter.trim()
        buildList {
            var header: ItemUI.Header? = null
            list.forEach { item ->
                when (item) {
                    is ItemUI.Header -> header = item
                    is ItemUI.Body -> if (item.title.contains(query, ignoreCase = true) || item.name.contains(query, ignoreCase = true)) {
                        header?.let(::add)
                        header = null
                        add(item)
                    }
                }
            }
        }
    }
}
