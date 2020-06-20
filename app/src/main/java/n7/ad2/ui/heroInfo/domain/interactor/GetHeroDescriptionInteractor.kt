package n7.ad2.ui.heroInfo.domain.interactor

import android.content.res.Resources
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.usecase.GetJsonHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetLocalHeroDescriptionFromJsonUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import javax.inject.Inject

class GetHeroDescriptionInteractor @Inject constructor(
        val getJsonHeroDescriptionUseCase: GetJsonHeroDescriptionUseCase,
        val getLocalHeroDescriptionFromJsonUseCase: GetLocalHeroDescriptionFromJsonUseCase,
        val getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase
) {

    suspend operator fun invoke( localHero: LocalHero, locale: String,theme: Resources.Theme): VOHeroDescription {
        val json = getJsonHeroDescriptionUseCase(localHero.assetsPath, locale)
        val localHeroDescription = getLocalHeroDescriptionFromJsonUseCase(json)
        return getVOHeroDescriptionUseCase(localHeroDescription, localHero, theme)
    }

}