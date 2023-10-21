package n7.ad2.items.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domain.model.Item

interface GetItemsUseCase {
    operator fun invoke(): Flow<List<Item>>
}
