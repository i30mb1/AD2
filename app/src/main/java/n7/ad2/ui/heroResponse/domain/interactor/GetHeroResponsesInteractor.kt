package n7.ad2.ui.heroResponse.domain.interactor

import n7.ad2.data.source.local.HeroLocale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetSavedHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.ConvertLocalHeroToVOHeroUseCase
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
    private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
    private val convertLocalHeroToVOHeroUseCase: ConvertLocalHeroToVOHeroUseCase,
    private val getSavedHeroResponseUseCase: GetSavedHeroResponseUseCase
) {

    suspend operator fun invoke(localHero: LocalHero, locale: HeroLocale): Result<List<VOResponse>> {
        return try {
            val json = getJsonHeroResponseUseCase(localHero.assetsPath, locale)
            val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
            val savedHeroResponses = getSavedHeroResponseUseCase(localHero.name)
            Result.success(convertLocalHeroToVOHeroUseCase.invoke(localHero, localHeroResponses, savedHeroResponses))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}