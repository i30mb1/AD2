package ad2.n7.news.internal.domain.usecase

import ad2.n7.news.internal.domain.model.NewsVO
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(): List<NewsVO> = withContext(dispatchers.IO) {
        buildList {
            NewsVO("Hello")
        }
    }

}