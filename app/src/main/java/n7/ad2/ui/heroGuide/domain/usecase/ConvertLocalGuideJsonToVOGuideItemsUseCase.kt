package n7.ad2.ui.heroGuide.domain.usecase;

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.ui.heroGuide.domain.adapter.toVOGuideBestVersus
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOGuideBestVersus
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import n7.ad2.ui.heroGuide.domain.vo.VOGuideTitle
import javax.inject.Inject

class ConvertLocalGuideJsonToVOGuideItemsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val application: Application,
) {

    @OptIn(ExperimentalStdlibApi::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(list: List<LocalGuideJson>): List<VOGuideItem> = withContext(ioDispatcher) {
        val item = list.getOrNull(0) ?: return@withContext emptyList()
        buildList {
            add(VOGuideTitle(application.getString(R.string.best_versus)))
            add(VOGuideBestVersus(item.heroBestVersus.toVOGuideBestVersus(application)))
        }
    }
}