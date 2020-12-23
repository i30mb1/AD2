package n7.ad2.ui.heroGuide.domain.usecase;

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.ui.heroGuide.domain.adapter.toVOEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.adapter.toVOHardToWinHeroes
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOHardToWinHeroes
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
        val item = list.getOrNull(list.lastIndex) ?: return@withContext emptyList()
        buildList {
            add(VOGuideTitle(application.getString(R.string.easy_to_win_heroes)))
            add(VOHardToWinHeroes(item.easyToWinHeroList.toVOEasyToWinHeroes(application)))
            add(VOGuideTitle(application.getString(R.string.hard_to_win_heroes)))
            add(VOHardToWinHeroes(item.hardToWinHeroList.toVOHardToWinHeroes(application)))
        }
    }
}