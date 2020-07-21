package n7.ad2.ui.heroResponse.domain.usecase

import android.app.Application
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import java.io.File
import javax.inject.Inject

class GetVOHeroResponsesUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    suspend operator fun invoke(
        heroName: String,
        localHeroResponses: List<LocalHeroResponsesItem>,
        savedHeroResponses: List<String>
    ): List<VOResponse> = withContext(ioDispatcher) {
        val result = mutableListOf<VOResponse>()

        localHeroResponses.forEach {
            result.add(VOResponseHeader(it.category))
            it.responses.forEach { response ->
                val titleForFile = response.title.replace(" ", "_").plus(".mp3")
                val savedInMemory = savedHeroResponses.contains(titleForFile)
                var audioUrl = response.audioUrl
                if (savedInMemory) audioUrl = application.getExternalFilesDir(Repository.DIRECTORY_RESPONSES + File.separator + heroName + File.separator + titleForFile)!!.toUri().toString()
                result.add(VOResponseBody(audioUrl, heroName, response.title, emptyList(), titleForFile, savedInMemory))
            }
        }

        result
    }

}