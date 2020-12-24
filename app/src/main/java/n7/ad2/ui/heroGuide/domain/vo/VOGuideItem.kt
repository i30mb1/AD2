package n7.ad2.ui.heroGuide.domain.vo

import n7.ad2.ui.heroGuide.VOHeroFlowItem

sealed class VOGuideItem
data class VOGuideTitle(val title: String) : VOGuideItem()
data class VOHardToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()
data class VOEasyToWinHeroes(val list: List<VOHeroFlowItem>) : VOGuideItem()