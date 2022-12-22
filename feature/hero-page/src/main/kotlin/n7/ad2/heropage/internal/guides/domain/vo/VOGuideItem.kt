package n7.ad2.heropage.internal.guides.domain.vo

import n7.ad2.heropage.internal.guides.VOHeroFlowHeroItem
import n7.ad2.heropage.internal.guides.VOHeroFlowItem
import n7.ad2.heropage.internal.guides.VOHeroFlowSpell
import n7.ad2.heropage.internal.guides.VOHeroFlowStartingHeroItem

sealed class VOGuideItem
data class VOGuideTitle(val title: String) : VOGuideItem()
data class VOGuideHardToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideEasyToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideInfoLine(val title: String) : VOGuideItem()
data class VOGuideSpellBuild(val list: List<VOHeroFlowSpell>) : VOGuideItem()
data class VOGuideStartingHeroItems(val list: List<VOHeroFlowStartingHeroItem>) : VOGuideItem()
data class VOGuideHeroItems(val list: List<VOHeroFlowHeroItem>) : VOGuideItem()