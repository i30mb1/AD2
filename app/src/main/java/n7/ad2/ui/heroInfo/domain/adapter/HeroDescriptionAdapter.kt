package n7.ad2.ui.heroInfo.domain.adapter

import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription

fun LocalHeroDescription.toVO(heroAssetsPath: String): VOHeroDescription {
    val voHeroDescription = VOHeroDescription()

    voHeroDescription.heroImagePath = "file:///android_asset/$heroAssetsPath/full.png"

    return voHeroDescription
}