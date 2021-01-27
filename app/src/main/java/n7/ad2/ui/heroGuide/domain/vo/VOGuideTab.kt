package n7.ad2.ui.heroGuide.domain.vo

sealed class VOGuideTab
data class VOGuideTabNumber(val number: String) : VOGuideTab()