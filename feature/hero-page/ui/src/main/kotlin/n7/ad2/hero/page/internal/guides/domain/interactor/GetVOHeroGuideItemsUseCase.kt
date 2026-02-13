package n7.ad2.hero.page.internal.guides.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import n7.ad2.hero.page.internal.guides.domain.usecase.ConvertLocalGuideJsonToVOGuideItemsUseCase
import n7.ad2.hero.page.internal.guides.domain.usecase.LoadNewHeroGuideUseCase
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase
import javax.inject.Inject

class GetVOHeroGuideItemsUseCase @Inject constructor(
    private val getGuideForHeroUseCase: GetGuideForHeroUseCase,
    private val shouldWeLoadNewHeroGuidesInteractor: ShouldWeLoadNewHeroGuidesInteractor,
    private val loadNewHeroGuideUseCase: LoadNewHeroGuideUseCase,
    private val convertLocalGuideJsonToVOGuideItemsUseCase: ConvertLocalGuideJsonToVOGuideItemsUseCase,
) {

    operator fun invoke(heroName: String): Flow<List<VOGuideItem>> = getGuideForHeroUseCase(heroName)
        .onStart {
//                if (shouldWeLoadNewHeroGuidesInteractor(heroName))
            loadNewHeroGuideUseCase(heroName)
        }
        .map { convertLocalGuideJsonToVOGuideItemsUseCase(it) }
        .distinctUntilChanged()
}
