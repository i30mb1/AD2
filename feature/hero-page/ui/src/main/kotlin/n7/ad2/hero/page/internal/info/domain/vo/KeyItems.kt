package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase

private const val KEY_ITEMS = 4

/** Mid game items are what the hero is built around, early ones are mostly boots and stats. */
fun VOGuide.keyItems(): List<VOGuideItem> = phases.firstOrNull { it.phase == VOGuidePhase.Phase.MID }?.items.orEmpty().take(KEY_ITEMS)
