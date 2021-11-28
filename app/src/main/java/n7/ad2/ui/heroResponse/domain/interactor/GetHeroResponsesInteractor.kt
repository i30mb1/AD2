package n7.ad2.ui.heroResponse.domain.interactor

import n7.ad2.data.source.local.Locale
import n7.ad2.database_heroes.api.model.LocalHero
import n7.ad2.ui.heroResponse.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetSavedHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.utils.ResultState
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
    private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val getSavedHeroResponseUseCase: GetSavedHeroResponseUseCase,
) {

    suspend operator fun invoke(localHero: LocalHero, locale: Locale): ResultState<List<VOResponse>> {
        return try {
            val json = getJsonHeroResponseUseCase(localHero.name, locale)
            val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
            val savedHeroResponses = getSavedHeroResponseUseCase(localHero.name)
            ResultState.success(convertLocalHeroToVOResponseUseCase.invoke(localHero, localHeroResponses, savedHeroResponses))
        } catch (e: Exception) {
            ResultState.failure(e)
        }
    }
}