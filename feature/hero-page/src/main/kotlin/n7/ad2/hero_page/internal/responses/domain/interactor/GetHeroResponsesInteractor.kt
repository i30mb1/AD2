package n7.ad2.hero_page.internal.responses.domain.interactor

import n7.ad2.AppLocale
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.hero_page.internal.responses.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.hero_page.internal.responses.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.hero_page.internal.responses.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.hero_page.internal.responses.domain.usecase.GetSavedHeroResponseUseCase
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
    private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val getSavedHeroResponseUseCase: GetSavedHeroResponseUseCase,
) {

    suspend operator fun invoke(localHero: LocalHero, appLocale: AppLocale): List<VOResponse> {
        val json = getJsonHeroResponseUseCase(localHero.name, appLocale)
        val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
        val savedHeroResponses = getSavedHeroResponseUseCase(localHero.name)
        return convertLocalHeroToVOResponseUseCase.invoke(localHero, localHeroResponses, savedHeroResponses)
    }
}