package n7.ad2.hero.page.internal.responses.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero.page.internal.responses.domain.model.LocalHeroResponsesItem
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponseImage
import n7.ad2.core.ui.adapter.HeaderViewHolder
import java.io.File
import javax.inject.Inject

class ConvertLocalHeroToVOResponseUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    suspend operator fun invoke(heroName: String, localHeroResponses: List<LocalHeroResponsesItem>, savedHeroResponses: List<File>): List<VOResponse> = withContext(dispatchers.IO) {
        val result = mutableListOf<VOResponse>()
        val mutableSaveHeroResponses = savedHeroResponses.toMutableList()

        localHeroResponses.forEach {
            result.add(VOResponse.Title(HeaderViewHolder.Data(it.category)))
            it.responses.forEach { response ->
                val titleForSavedFile = response.audioUrl.substringBeforeLast(".mp3").substringAfterLast("/") + ".mp3"

                val iterator = mutableSaveHeroResponses.iterator()
                var savedInMemory = false
                var audioUrl = response.audioUrl

                while (iterator.hasNext() && !savedInMemory) {
                    val item = iterator.next()
                    if (item.endsWith(titleForSavedFile)) {
                        savedInMemory = true
                        audioUrl = item.toString()
                        iterator.remove()
                    }
                }

                // heroes/sven/minimap.webp or items/blink_dagger/full.webp, core/parser keeps only the ones the app has
                val icons = response.icons.map { path -> VOResponseImage(path.substringBeforeLast("/").substringAfterLast("/"), "file:///android_asset/$path") }

                result.add(VOResponse.Body(heroName, response.title, icons, titleForSavedFile, savedInMemory, audioUrl, response.isArcane))
            }
        }

        result.toList()
    }
}
