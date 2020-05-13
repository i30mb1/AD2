package n7.ad2.ui.heroInfo.domain.adapter

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

fun LocalHeroDescription.toVO(localHero: LocalHero): VOHeroDescription {
    val voHeroDescription = VOHeroDescription()

    voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/full.png"

    val voSpellList: List<VOSpell> = this.abilities.map {
        VOSpell().apply {
            selected = false
            image = "file:///android_asset/spells/${it.spellName}.png"
        }
    }
    voHeroDescription.spells = voSpellList

    return voHeroDescription
}