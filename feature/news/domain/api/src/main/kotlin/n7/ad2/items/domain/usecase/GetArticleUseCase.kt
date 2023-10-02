package n7.ad2.items.domain.usecase

import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domain.model.Article

interface GetArticleUseCase {
    operator fun invoke(newsID: Int): Flow<Article>
}
