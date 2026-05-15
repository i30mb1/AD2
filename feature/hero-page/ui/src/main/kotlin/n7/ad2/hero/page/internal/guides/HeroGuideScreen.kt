package n7.ad2.hero.page.internal.guides

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.hero.page.internal.guides.components.GuideInfoLineItem
import n7.ad2.hero.page.internal.guides.components.GuideTitleItem
import n7.ad2.hero.page.internal.guides.components.chips.HeroChip
import n7.ad2.hero.page.internal.guides.components.chips.ItemChip
import n7.ad2.hero.page.internal.guides.components.chips.SpellChip
import n7.ad2.hero.page.internal.guides.components.chips.StartingItemChip
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideEasyToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHardToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHeroItems
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideInfoLine
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideSpellBuild
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideStartingHeroItems
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideTitle

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun HeroGuideScreen(viewModel: HeroGuideViewModel, heroName: String) {
    val items by viewModel.loadHeroWithGuides(heroName).observeAsState(emptyList())
    LazyColumn {
        items.forEachIndexed { idx, guideItem ->
            when (guideItem) {
                is VOGuideTitle -> stickyHeader(key = "t-$idx") { GuideTitleItem(guideItem.title) }
                is VOGuideInfoLine -> item(key = "il-$idx") { GuideInfoLineItem(guideItem.title) }
                is VOGuideEasyToWinHeroes -> item(key = "ew-$idx") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        guideItem.list.forEach { HeroChip(it, good = true) }
                    }
                }
                is VOGuideHardToWinHeroes -> item(key = "hw-$idx") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        guideItem.list.forEach { HeroChip(it, good = false) }
                    }
                }
                is VOGuideSpellBuild -> item(key = "sb-$idx") {
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        guideItem.list.forEach { SpellChip(it) }
                    }
                }
                is VOGuideStartingHeroItems -> item(key = "si-$idx") {
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        guideItem.list.forEach { StartingItemChip(it) }
                    }
                }
                is VOGuideHeroItems -> item(key = "hi-$idx") {
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        guideItem.list.forEach { ItemChip(it) }
                    }
                }
            }
        }
    }
}
