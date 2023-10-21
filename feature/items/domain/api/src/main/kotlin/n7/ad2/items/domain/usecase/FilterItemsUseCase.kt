package n7.ad2.items.domain.usecase

import n7.ad2.items.domain.model.Item

interface FilterItemsUseCase {
    suspend operator fun invoke(list: List<Item>, filter: String): List<Item>
}
