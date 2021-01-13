package n7.ad2.ui.heroGuide.domain.vo

import n7.ad2.ui.heroGuide.VOHeroFlowItem
import n7.ad2.ui.heroGuide.VOHeroFlowSpell
import n7.ad2.ui.heroGuide.VOHeroFlowStartingHeroItem

sealed class VOGuideItem
data class VOGuideTitle(val title: String) : VOGuideItem()
data class VOGuideHardToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideEasyToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideInfoLine(val title: String) : VOGuideItem()
data class VOGuideSpellBuild(val list: List<VOHeroFlowSpell>) : VOGuideItem()
data class VOGuideStartingHeroItems(val list: List<VOHeroFlowStartingHeroItem>) : VOGuideItem()