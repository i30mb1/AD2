package n7.ad2.ui.heroInfo.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class GetVOHeroDescriptionUseCase constructor(
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero): VOHeroDescription = withContext(ioDispatcher) {
        val voHeroDescription = VOHeroDescription()

        voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/full.png"

        val voSpellList: List<VOSpell> = localHeroDescription.abilities.map {
            VOSpell().apply {
                selected = false
                image = "file:///android_asset/spells/${it.spellName}.png"
            }
        }
        voHeroDescription.spells = voSpellList

        voHeroDescription
    }

}