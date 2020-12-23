package n7.ad2.ui.heroGuide.domain.vo

import android.view.View

sealed class VOGuideItem
data class VOGuideTitle(val title: String) : VOGuideItem()
data class VOHardToWinHeroes(val list: List<View>) : VOGuideItem()
data class VOEasyToWinHeroes(val list: List<View>) : VOGuideItem()