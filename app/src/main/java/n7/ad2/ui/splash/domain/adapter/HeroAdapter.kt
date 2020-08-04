package n7.ad2.ui.splash.domain.adapter

import n7.ad2.ui.splash.domain.model.AssetsHero
import n7.ad2.data.source.local.model.LocalHero

fun AssetsHero.toLocalHero(): LocalHero {
    return LocalHero(
            name = this.name,
            assetsPath = this.assetsPath,
            mainAttr = this.mainAttr
    )
}