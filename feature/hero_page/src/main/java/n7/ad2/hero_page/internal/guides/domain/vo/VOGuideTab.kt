package n7.ad2.hero_page.internal.guides.domain.vo

sealed class VOGuideTab
data class VOGuideTabNumber(val number: String) : VOGuideTab()