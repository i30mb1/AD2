package n7.ad2.heropage.internal.guides.domain.vo

sealed class VOGuideTab
data class VOGuideTabNumber(val number: String) : VOGuideTab()