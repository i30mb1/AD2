package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroInfo.domain.model.LocalHeroResponses
import n7.ad2.ui.heroInfo.domain.vo.VOResponse
import n7.ad2.ui.heroInfo.domain.vo.VOResponseBody
import n7.ad2.ui.heroInfo.domain.vo.VOResponseHeader
import javax.inject.Inject

class GetVOHeroResponsesUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    suspend operator fun invoke(localHeroResponses: LocalHeroResponses): List<VOResponse> = withContext(ioDispatcher) {
        val result = mutableListOf<VOResponse>()

        localHeroResponses.forEach {
            result.add(VOResponseHeader(it.category))
            it.responses.forEach { response ->
                result.add(VOResponseBody(response.title, response.audioUrl, emptyList()))
            }
        }

        result
    }

}