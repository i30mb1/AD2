package n7.ad2.hero_page.internal.guides.domain.interactor;

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import n7.ad2.hero_page.internal.guides.domain.usecase.ConvertLocalGuideJsonToVOGuideItemsUseCase
import n7.ad2.hero_page.internal.guides.domain.usecase.ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase
import n7.ad2.hero_page.internal.guides.domain.usecase.GetLocalHeroWithGuidesUseCase
import n7.ad2.hero_page.internal.guides.domain.usecase.LoadNewHeroGuideUseCase
import n7.ad2.hero_page.internal.guides.domain.vo.VOGuideItem
import javax.inject.Inject

class GetVOHeroGuideItemsUseCase @Inject constructor(
    private val getLocalHeroWithGuidesUseCase: GetLocalHeroWithGuidesUseCase,
    private val shouldWeLoadNewHeroGuidesInteractor: ShouldWeLoadNewHeroGuidesInteractor,
    private val loadNewHeroGuideUseCase: LoadNewHeroGuideUseCase,
    private val convertLocalHeroWithGuidesToLocalGuideJsonUseCase: ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase,
    private val convertLocalGuideJsonToVOGuideItemsUseCase: ConvertLocalGuideJsonToVOGuideItemsUseCase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    operator fun invoke(heroName: String): Flow<List<VOGuideItem>> {
        return getLocalHeroWithGuidesUseCase(heroName)
            .onStart {
//                if (shouldWeLoadNewHeroGuidesInteractor(heroName))
                loadNewHeroGuideUseCase(heroName)
            }
            .map { convertLocalHeroWithGuidesToLocalGuideJsonUseCase(it) }
            .map { convertLocalGuideJsonToVOGuideItemsUseCase(it) }
            .distinctUntilChanged()
    }

}