package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuideHeroes
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuideItems
import n7.ad2.hero.page.internal.guides.domain.adapter.toVOGuidePhases
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.heroes.domain.model.Guide
import javax.inject.Inject

class ConvertGuideToVOGuideUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    /** The last guide is the freshest one, null when nothing is loaded yet. */
    suspend operator fun invoke(list: List<Guide>): VOGuide? = withContext(dispatchers.IO) {
        val guide = list.lastOrNull() ?: return@withContext null
        VOGuide(
            winrate = guide.heroWinrate,
            matches = guide.heroPopularity,
            startingItems = guide.heroStartingHeroItemsList.toVOGuideItems(),
            phases = guide.heroItemsList.toVOGuidePhases(),
            easyToWin = guide.easyToWinHeroList.toVOGuideHeroes(),
            hardToWin = guide.hardToWinHeroList.toVOGuideHeroes(),
        )
    }
}
