package n7.ad2.ui.heroInfo.domain.interactor

import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.usecase.GetJsonHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetLocalHeroDescriptionFromJsonUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import javax.inject.Inject

class GetHeroDescriptionInteractor @Inject constructor(
        val getJsonHeroDescriptionUseCase: GetJsonHeroDescriptionUseCase,
        val getLocalHeroDescriptionFromJsonUseCase: GetLocalHeroDescriptionFromJsonUseCase,
        val getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase
) {

    @ExperimentalStdlibApi
    suspend operator fun invoke(localHero: LocalHero, locale: Locale): List<VODescription> {
        val json = getJsonHeroDescriptionUseCase(localHero.name, locale)
        val localHeroDescription = getLocalHeroDescriptionFromJsonUseCase(json)
        return getVOHeroDescriptionUseCase(localHeroDescription, localHero)
    }

}