package n7.ad2.hero.page.internal.guides.domain.vo

import n7.ad2.hero.page.internal.guides.VOHeroFlowHeroItem
import n7.ad2.hero.page.internal.guides.VOHeroFlowItem
import n7.ad2.hero.page.internal.guides.VOHeroFlowSpell
import n7.ad2.hero.page.internal.guides.VOHeroFlowStartingHeroItem

sealed class VOGuideItem
data class VOGuideTitle(val title: String) : VOGuideItem()
data class VOGuideHardToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideEasyToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOGuideInfoLine(val title: String) : VOGuideItem()
data class VOGuideSpellBuild(val list: List<VOHeroFlowSpell>) : VOGuideItem()
data class VOGuideStartingHeroItems(val list: List<VOHeroFlowStartingHeroItem>) : VOGuideItem()
data class VOGuideHeroItems(val list: List<VOHeroFlowHeroItem>) : VOGuideItem()