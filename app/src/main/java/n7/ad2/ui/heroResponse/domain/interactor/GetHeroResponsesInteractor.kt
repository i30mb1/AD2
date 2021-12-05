package n7.ad2.ui.heroResponse.domain.interactor

import n7.ad2.data.source.local.Locale
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.ui.heroResponse.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetSavedHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
    private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val getSavedHeroResponseUseCase: GetSavedHeroResponseUseCase,
) {

    suspend operator fun invoke(localHero: LocalHero, locale: Locale): List<VOResponse> {
        val json = getJsonHeroResponseUseCase(localHero.name, locale)
        val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
        val savedHeroResponses = getSavedHeroResponseUseCase(localHero.name)
        return convertLocalHeroToVOResponseUseCase.invoke(localHero, localHeroResponses, savedHeroResponses)
    }
}