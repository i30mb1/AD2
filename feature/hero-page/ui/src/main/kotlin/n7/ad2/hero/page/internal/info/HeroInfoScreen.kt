package n7.ad2.hero.page.internal.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.common.SectionChips
import n7.ad2.hero.page.internal.info.components.AbilitiesSection
import n7.ad2.hero.page.internal.info.components.CountersSection
import n7.ad2.hero.page.internal.info.components.FacetsSection
import n7.ad2.hero.page.internal.info.components.ItemsSection
import n7.ad2.hero.page.internal.info.components.LoreSection
import n7.ad2.hero.page.internal.info.components.StatsSection
import n7.ad2.hero.page.internal.info.components.StrategySection
import n7.ad2.hero.page.internal.info.components.TalentsSection
import n7.ad2.hero.page.internal.info.domain.vo.InfoSection
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.sections
import n7.ad2.hero.page.internal.pager.foldsHeroBanner

@Composable
internal fun HeroInfoScreen(viewModel: HeroInfoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        HeroInfoViewModel.State.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { LoadingAnimation() }

        is HeroInfoViewModel.State.Error -> ErrorScreen(error = s.error)
        is HeroInfoViewModel.State.Data -> HeroInfoContent(s.info)
    }
}

/**
 * One section at a time, picked by the chips: the page stays short and nothing has to be unfolded.
 * The chips stay above the content and every section scrolls on its own, so a new one always opens from its top.
 */
@Composable
private fun HeroInfoContent(info: VOHeroInfo) {
    val description = info.description
    val sections = remember(info) { info.sections() }
    var selectedName by rememberSaveable { mutableStateOf(sections.first().name) }
    val selected = sections.firstOrNull { it.name == selectedName } ?: sections.first()

    val abilities = info.abilityGroups.flatMap { it.abilities }
    val defaultAbility = info.abilityGroups.firstOrNull { it.group == VOAbilityGroup.Group.MAIN }?.abilities?.firstOrNull() ?: abilities.firstOrNull()
    var selectedAbilityName by rememberSaveable { mutableStateOf(defaultAbility?.name) }
    val selectedAbility = abilities.firstOrNull { it.name == selectedAbilityName } ?: defaultAbility

    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(Modifier.fillMaxSize()) {
        SectionChips(
            titles = sections.map { stringResource(it.title) },
            selected = sections.indexOf(selected),
            onSelect = { index -> selectedName = sections[index].name },
        )
        key(selected) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .foldsHeroBanner()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = bottomInset + 16.dp),
            ) {
                when (selected) {
                    InfoSection.ABILITIES -> if (selectedAbility != null) {
                        AbilitiesSection(
                            groups = info.abilityGroups,
                            selected = selectedAbility,
                            onSelect = { ability -> selectedAbilityName = ability.name },
                            changes = description.changes,
                        )
                    }
                    InfoSection.STATS -> StatsSection(description.stats)
                    InfoSection.TALENTS -> TalentsSection(description.talents)
                    InfoSection.FACETS -> FacetsSection(description.facets)
                    InfoSection.STRATEGY -> StrategySection(description.wiki.playstyle, description.wiki.tips)
                    InfoSection.COUNTERS -> CountersSection(description.wiki.counters)
                    InfoSection.ITEMS -> ItemsSection(description.wiki.recommendedItems)
                    InfoSection.LORE -> LoreSection(description.bio, description.trivia)
                }
            }
        }
    }
}

private val InfoSection.title: Int
    get() = when (this) {
        InfoSection.ABILITIES -> R.string.info_abilities
        InfoSection.STATS -> R.string.info_stats
        InfoSection.TALENTS -> R.string.info_talents
        InfoSection.FACETS -> R.string.info_facets
        InfoSection.STRATEGY -> R.string.info_strategy
        InfoSection.COUNTERS -> R.string.info_counters
        InfoSection.ITEMS -> R.string.info_items_short
        InfoSection.LORE -> R.string.info_lore
    }
