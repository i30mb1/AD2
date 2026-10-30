package n7.ad2.hero.page.internal.guides.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import n7.ad2.hero.page.internal.guides.domain.usecase.ConvertGuideToVOGuideUseCase
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase
import javax.inject.Inject

class GetVOHeroGuideUseCase @Inject constructor(
    private val getGuideForHeroUseCase: GetGuideForHeroUseCase,
    private val convertGuideToVOGuideUseCase: ConvertGuideToVOGuideUseCase,
) {

    operator fun invoke(heroName: String): Flow<VOGuide?> = getGuideForHeroUseCase(heroName)
        .map { convertGuideToVOGuideUseCase(it) }
        .distinctUntilChanged()
}
