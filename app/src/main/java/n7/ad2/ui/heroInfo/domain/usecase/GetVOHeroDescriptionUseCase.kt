package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import java.lang.StringBuilder
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    companion object {
        const val SEPARATOR = "- "
    }

   private fun List<String>.toListWithDash(): String {
        val builder = StringBuilder()
        forEachIndexed { index, text ->
            builder.append(SEPARATOR)
            builder.append(text)
           if(index != lastIndex) builder.append(System.lineSeparator())
        }
        return builder.toString()
    }

    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero): VOHeroDescription = withContext(ioDispatcher) {
        val voHeroDescription = VOHeroDescription()

        voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/full.png"

        val voSpellList: List<VOSpell> = localHeroDescription.abilities.map {
            val descriptions = mutableListOf<VODescription>().apply {
                add(VODescription(it.spellName, it.hotKey, it.legacyKey, it.description))
                if (it.story != null) add(VODescription(title = application.getString(R.string.hero_fragment_story), body = it.story))
                add(VODescription(title = application.getString(R.string.hero_fragment_notes), body = it.notes.toListWithDash()))

            }

            VOSpell().apply {
                name = it.spellName
                selected = false
                image = "file:///android_asset/spells/${it.spellName}.png"
                listVODescriptions = descriptions
                spellAudio = it.spellAudio
            }
        }
        voHeroDescription.spells = voSpellList



        voHeroDescription
    }

}