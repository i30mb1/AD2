package n7.ad2.ui.splash.domain.usecase

import n7.ad2.data.source.local.model.AssetsHero
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.splash.domain.adapter.toLocalHero
import javax.inject.Inject

class ConvertAssetsHeroListToLocalHeroList @Inject constructor() {

    operator fun invoke(assetsHeroList: List<AssetsHero>): List<LocalHero> {
        return assetsHeroList.map {
            it.toLocalHero()
        }
    }
}