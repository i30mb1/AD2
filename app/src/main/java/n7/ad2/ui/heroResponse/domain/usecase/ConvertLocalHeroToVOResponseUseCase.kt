package n7.ad2.ui.heroResponse.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseImage
import n7.ad2.ui.heroResponse.domain.vo.VOResponseTitle
import java.io.File
import javax.inject.Inject

class ConvertLocalHeroToVOResponseUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(
        localHero: LocalHero,
        localHeroResponses: List<LocalHeroResponsesItem>,
        savedHeroResponses: List<File>,
    ): List<VOResponse> = withContext(ioDispatcher) {
        val result = mutableListOf<VOResponse>()
        val savedResponses = savedHeroResponses as? MutableList ?: mutableListOf()
        var fileToRemoveFromSavedResponses: File? = null

        localHeroResponses.forEach {
            result.add(VOResponseTitle(it.category))
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
                    val heroName = iconPath.substringBeforeLast("/").substringAfter("/")
                    val url = "file:///android_asset/$iconPath"
                    VOResponseImage(heroName, url)
                }
                if (response.isArcane) {
                    val arcaneItem = VOResponseImage("Arcane", "file:///android_asset/heroes/${localHero.name}/arcane.png")
                    (icons as MutableList).add(0, arcaneItem)
                }

                result.add(VOResponseBody(audioUrl, localHero.name, response.title, icons, titleForSavedFile, savedInMemory))
            }
        }

        result
    }

}