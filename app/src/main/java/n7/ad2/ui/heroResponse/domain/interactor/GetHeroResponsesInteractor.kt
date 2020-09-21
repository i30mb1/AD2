package n7.ad2.ui.heroResponse.domain.interactor

import n7.ad2.data.source.local.HeroLocale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetSavedHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetVOHeroResponsesUseCase
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
    private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
    private val getVOHeroResponsesUseCase: GetVOHeroResponsesUseCase,
    private val getSavedHeroResponseUseCase: GetSavedHeroResponseUseCase
) {

    suspend operator fun invoke(localHero: LocalHero, locale: HeroLocale): List<VOResponse> {
        val json = getJsonHeroResponseUseCase(localHero.assetsPath, locale)
        val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
        val savedHeroResponses = getSavedHeroResponseUseCase(localHero.name)
        return getVOHeroResponsesUseCase.invoke(localHero.name, localHeroResponses, savedHeroResponses)
    }
}