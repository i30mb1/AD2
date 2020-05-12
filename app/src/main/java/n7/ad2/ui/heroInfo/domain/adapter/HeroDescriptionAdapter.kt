package n7.ad2.ui.heroInfo.domain.adapter

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription

fun LocalHeroDescription.toVO(localHero: LocalHero): VOHeroDescription {
    val voHeroDescription = VOHeroDescription()

    voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/full.png"

    return voHeroDescription
}