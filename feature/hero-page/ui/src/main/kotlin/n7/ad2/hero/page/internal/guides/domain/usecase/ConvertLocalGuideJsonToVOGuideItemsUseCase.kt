package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.heropage.ui.R
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOEasyToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuideHeroItems
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuideSpellBuild
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuideStartingHeroItems
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOHardToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideInfoLine
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideTitle
import n7.ad2.heroes.domain.Guide
import javax.inject.Inject

class ConvertLocalGuideJsonToVOGuideItemsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val res: Resources,
) {

    suspend operator fun invoke(list: List<Guide>): List<VOGuideItem> = withContext(dispatchers.IO) {
        val item = list.getOrNull(list.lastIndex) ?: return@withContext emptyList()
        buildList {
            add(VOGuideInfoLine(res.getString(R.string.hero_popularity, item.heroWinrate)))
            add(VOGuideInfoLine(res.getString(R.string.hero_pickrate, item.heroPopularity)))
            add(VOGuideTitle(res.getString(R.string.starting_items)))
            add(item.heroStartingHeroItemsList.toVOGuideStartingHeroItems())
            add(VOGuideTitle(res.getString(R.string.guide_items)))
            add(item.heroItemsList.toVOGuideHeroItems())
            add(VOGuideTitle(res.getString(R.string.spell_build)))
            add(item.heroSpellsList.toVOGuideSpellBuild())
            add(VOGuideTitle(res.getString(R.string.easy_to_win_heroes)))
            add(item.easyToWinHeroList.toVOEasyToWinHeroes())
            add(VOGuideTitle(res.getString(R.string.hard_to_win_heroes)))
            add(item.hardToWinHeroList.toVOHardToWinHeroes())
        }
    }
}
