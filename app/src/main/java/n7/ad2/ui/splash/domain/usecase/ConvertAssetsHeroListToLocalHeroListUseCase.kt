package n7.ad2.ui.splash.domain.usecase

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.splash.domain.model.AssetsHero
import javax.inject.Inject

class ConvertAssetsHeroListToLocalHeroListUseCase @Inject constructor() {

    operator fun invoke(assetsHeroList: List<AssetsHero>): List<LocalHero> {
        return assetsHeroList.map {
            LocalHero(
                name = it.name,
                mainAttr = it.mainAttr,
                viewedByUser = false,
            )
        }
    }
}