package n7.ad2.ui.heroResponse.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import javax.inject.Inject

class GetVOHeroResponsesUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    suspend operator fun invoke(heroName: String, localHeroResponses: List<LocalHeroResponsesItem>): List<VOResponse> = withContext(ioDispatcher) {
        val result = mutableListOf<VOResponse>()

        localHeroResponses.forEach {
            result.add(VOResponseHeader(it.category))
            it.responses.forEach { response ->
                result.add(VOResponseBody(heroName, response.title, response.audioUrl, emptyList()))
            }
        }

        result
    }

}