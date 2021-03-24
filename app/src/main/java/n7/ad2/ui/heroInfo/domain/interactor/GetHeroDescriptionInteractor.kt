package n7.ad2.ui.heroInfo.domain.interactor

import n7.ad2.AD2Logger
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.usecase.GetJsonHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetLocalHeroDescriptionFromJsonUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import javax.inject.Inject

class GetHeroDescriptionInteractor @Inject constructor(
    private val getJsonHeroDescriptionUseCase: GetJsonHeroDescriptionUseCase,
    private val getLocalHeroDescriptionFromJsonUseCase: GetLocalHeroDescriptionFromJsonUseCase,
    private val getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase,
    private val logger: AD2Logger,
) {

    @ExperimentalStdlibApi
    suspend operator fun invoke(localHero: LocalHero, locale: Locale): List<VODescription> {
        logger.log("load ${localHero.name} description")
        val json = getJsonHeroDescriptionUseCase(localHero.name, locale)
        val localHeroDescription = getLocalHeroDescriptionFromJsonUseCase(json)
        return getVOHeroDescriptionUseCase(localHeroDescription, localHero)
    }

}