package n7.ad2.ui.splash.domain.adapter

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.splash.domain.model.AssetsHero

fun AssetsHero.toLocalHero(): LocalHero {
    return LocalHero(
        name = this.name,
        mainAttr = this.mainAttr,
        viewedByUser = false,
    )
}