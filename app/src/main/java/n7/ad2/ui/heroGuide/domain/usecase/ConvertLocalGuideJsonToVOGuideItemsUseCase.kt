package n7.ad2.ui.heroGuide.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import android.app.Application
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.ui.heroGuide.domain.adapter.toVOEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.adapter.toVOGuideHeroItems
import n7.ad2.ui.heroGuide.domain.adapter.toVOGuideSpellBuild
import n7.ad2.ui.heroGuide.domain.adapter.toVOGuideStartingHeroItems
import n7.ad2.ui.heroGuide.domain.adapter.toVOHardToWinHeroes
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOGuideInfoLine
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import n7.ad2.ui.heroGuide.domain.vo.VOGuideTitle
import javax.inject.Inject

class ConvertLocalGuideJsonToVOGuideItemsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val application: Application,
) {

    @OptIn(ExperimentalStdlibApi::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(list: List<LocalGuideJson>): List<VOGuideItem> = withContext(dispatchers.IO) {
        val item = list.getOrNull(list.lastIndex) ?: return@withContext emptyList()
        buildList {
            add(VOGuideInfoLine(application.getString(R.string.hero_popularity, item.heroWinrate)))
            add(VOGuideInfoLine(application.getString(R.string.hero_pickrate, item.heroPopularity)))
            add(VOGuideTitle(application.getString(R.string.starting_items)))
            add(item.detailedGuide.heroStartingHeroItemsList.toVOGuideStartingHeroItems())
            add(VOGuideTitle(application.getString(R.string.guide_items)))
            add(item.detailedGuide.heroItemsList.toVOGuideHeroItems())
            add(VOGuideTitle(application.getString(R.string.spell_build)))
            add(item.detailedGuide.heroSpellsList.toVOGuideSpellBuild())
            add(VOGuideTitle(application.getString(R.string.easy_to_win_heroes)))
            add(item.easyToWinHeroList.toVOEasyToWinHeroes())
            add(VOGuideTitle(application.getString(R.string.hard_to_win_heroes)))
            add(item.hardToWinHeroList.toVOHardToWinHeroes())
        }
    }
}