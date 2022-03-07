package n7.ad2.hero_page.internal.responses.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero_page.internal.responses.domain.model.LocalHeroResponsesItem
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseImage
import n7.ad2.repositories.HeroRepository
import n7.ad2.ui.adapter.HeaderViewHolder
import java.io.File
import javax.inject.Inject

class ConvertLocalHeroToVOResponseUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(
        heroName: String,
        localHeroResponses: List<LocalHeroResponsesItem>,
        savedHeroResponses: List<File>,
    ): List<VOResponse> = withContext(dispatchers.IO) {
        val result = mutableListOf<VOResponse>()
        val mutableSaveHeroResponses = savedHeroResponses.toMutableList()
        val iterator = mutableSaveHeroResponses.iterator()

        localHeroResponses.forEach {
            result.add(VOResponse.Title(HeaderViewHolder.Data(it.category)))
            it.responses.forEach { response ->
                val titleForSavedFile = response.audioUrl.substringBeforeLast(".mp3").substringAfterLast("/") + ".mp3"

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

                val icons = response.icons.map { iconPath ->
                    val iconHeroName = iconPath.substringBeforeLast("/").substringAfter("/")
                    val url = HeroRepository.getFullUrlHeroMinimap(iconHeroName)
                    VOResponseImage(iconHeroName, url)
                }
                if (response.isArcane) {
                    val arcaneItem = VOResponseImage("Arcane", HeroRepository.getFullUrlHeroArcane(heroName))
                    (icons as MutableList).add(0, arcaneItem)
                }

                result.add(VOResponse.Body(heroName, response.title, icons, titleForSavedFile, savedInMemory, audioUrl))
            }
        }

        result.toList()
    }

}