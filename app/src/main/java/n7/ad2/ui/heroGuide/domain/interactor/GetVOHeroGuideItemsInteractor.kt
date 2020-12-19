package n7.ad2.ui.heroGuide.domain.interactor;

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalGuideJsonToVOGuideItemsUseCase
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase
import n7.ad2.ui.heroGuide.domain.usecase.GetLocalHeroWithGuidesUseCase
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import javax.inject.Inject

class GetVOHeroGuideItemsInteractor @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val getLocalHeroWithGuidesUseCase: GetLocalHeroWithGuidesUseCase,
    private val convertLocalHeroWithGuidesToLocalGuideJsonUseCase: ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase,
    private val convertLocalGuideJsonToVOGuideItemsUseCase: ConvertLocalGuideJsonToVOGuideItemsUseCase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(heroName: String): Flow<List<VOGuideItem>> = withContext(ioDispatcher) {
        getLocalHeroWithGuidesUseCase(heroName)
            .map { convertLocalHeroWithGuidesToLocalGuideJsonUseCase(it) }
            .map { convertLocalGuideJsonToVOGuideItemsUseCase(it) }
    }
}