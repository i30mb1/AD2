package n7.ad2.ui.heroResponse.domain.interactor

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.usecase.GetJsonHeroResponseUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetLocalHeroResponsesFromJsonUseCase
import n7.ad2.ui.heroResponse.domain.usecase.GetVOHeroResponsesUseCase
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
        private val getJsonHeroResponseUseCase: GetJsonHeroResponseUseCase,
        private val getLocalHeroResponsesFromJsonUseCase: GetLocalHeroResponsesFromJsonUseCase,
        private val getVOHeroResponsesUseCase: GetVOHeroResponsesUseCase
) {

    suspend operator fun invoke(localHero: LocalHero, locale: String): List<VOResponse> {
        val json = getJsonHeroResponseUseCase(localHero.assetsPath, locale)
        val localHeroResponses = getLocalHeroResponsesFromJsonUseCase(json)
        return getVOHeroResponsesUseCase(localHero.name, localHeroResponses)
    }
}