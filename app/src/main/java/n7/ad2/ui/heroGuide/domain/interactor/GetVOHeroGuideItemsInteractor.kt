package n7.ad2.ui.heroGuide.domain.interactor;

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalGuideJsonToVOGuideItemsUseCase
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase
import n7.ad2.ui.heroGuide.domain.usecase.GetLocalHeroWithGuidesUseCase
import n7.ad2.ui.heroGuide.domain.usecase.LoadNewHeroGuideUseCase
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import n7.ad2.utils.ResultState
import javax.inject.Inject

class GetVOHeroGuideItemsInteractor @Inject constructor(
    private val getLocalHeroWithGuidesUseCase: GetLocalHeroWithGuidesUseCase,
    private val loadNewHeroGuideUseCase: LoadNewHeroGuideUseCase,
    private val convertLocalHeroWithGuidesToLocalGuideJsonUseCase: ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase,
    private val convertLocalGuideJsonToVOGuideItemsUseCase: ConvertLocalGuideJsonToVOGuideItemsUseCase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    operator fun invoke(
        heroName: String,
        loadingCallback: (ResultState<Unit>) -> Unit,
    ): Flow<List<VOGuideItem>> {
        return getLocalHeroWithGuidesUseCase(heroName)
            .onStart { loadNewHeroGuideUseCase(heroName, loadingCallback) }
            .map { convertLocalHeroWithGuidesToLocalGuideJsonUseCase(it) }
            .map { convertLocalGuideJsonToVOGuideItemsUseCase(it) }
    }

}