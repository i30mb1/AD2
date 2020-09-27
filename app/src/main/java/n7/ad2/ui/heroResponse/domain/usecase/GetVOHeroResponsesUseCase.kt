package n7.ad2.ui.heroResponse.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import java.io.File
import javax.inject.Inject

class GetVOHeroResponsesUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(
        heroName: String,
        localHeroResponses: List<LocalHeroResponsesItem>,
        savedHeroResponses: List<File>
    ): List<VOResponse> = withContext(ioDispatcher) {
        val result = mutableListOf<VOResponse>()
        val savedResponses = savedHeroResponses as? MutableList ?: mutableListOf()
        var fileToRemoveFromSavedResponses: File? = null

        localHeroResponses.forEach {
            result.add(VOResponseHeader(it.category))
            it.responses.forEach { response ->
                val titleForSavedFile = response.audioUrl.substringBeforeLast(".mp3").substringAfterLast("/") + ".mp3"

                var savedInMemory = false
                var audioUrl = response.audioUrl
                savedResponses.forEach endCycle@{ file ->
                    if (file.endsWith(titleForSavedFile)) {
                        savedInMemory = true
                        audioUrl = file.toString()
                        fileToRemoveFromSavedResponses = file
                        return@endCycle
                    }
                }
                // todo need to optimize this

//                savedResponses.remove(fileToRemoveFromSavedResponses)
                val icons = response.icons.map { iconPath ->
                    "file:///android_asset/$iconPath"
                }

                result.add(VOResponseBody(audioUrl, heroName, response.title, icons, titleForSavedFile, savedInMemory))
            }
        }

        result
    }

}