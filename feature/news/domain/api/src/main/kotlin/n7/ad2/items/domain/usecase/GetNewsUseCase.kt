package n7.ad2.items.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domain.model.News

interface GetNewsUseCase {
    suspend operator fun invoke(page: Int): Flow<List<News>>
}
