package n7.ad2.hero.page.internal.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.hero.page.internal.info.components.AbilitiesCard
import n7.ad2.hero.page.internal.info.components.AttributesCard
import n7.ad2.hero.page.internal.info.components.BioCard
import n7.ad2.hero.page.internal.info.components.ChangesCard
import n7.ad2.hero.page.internal.info.components.CountersCard
import n7.ad2.hero.page.internal.info.components.FacetsCard
import n7.ad2.hero.page.internal.info.components.HeroHeader
import n7.ad2.hero.page.internal.info.components.RecommendedItemsCard
import n7.ad2.hero.page.internal.info.components.RolesCard
import n7.ad2.hero.page.internal.info.components.StatsCard
import n7.ad2.hero.page.internal.info.components.StrategyCard
import n7.ad2.hero.page.internal.info.components.TalentsCard
import n7.ad2.hero.page.internal.info.components.TriviaCard
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

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

@Composable
private fun HeroInfoContent(info: VOHeroInfo) {
    val description = info.description
    val abilities = info.abilityGroups.flatMap { it.abilities }
    val defaultAbility = info.abilityGroups.firstOrNull { it.group == VOAbilityGroup.Group.MAIN }?.abilities?.firstOrNull() ?: abilities.firstOrNull()
    var selectedAbilityName by rememberSaveable { mutableStateOf(defaultAbility?.name) }
    val selectedAbility = abilities.firstOrNull { it.name == selectedAbilityName } ?: defaultAbility

    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        contentPadding = PaddingValues(top = 12.dp, bottom = bottomInset + 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") { HeroHeader(info.heroImageUrl, description) }
        item(key = "attributes") { AttributesCard(description) }
        item(key = "stats") { StatsCard(description.stats, description.wiki) }
        if (description.roles.isNotEmpty()) item(key = "roles") { RolesCard(description.roles) }
        if (selectedAbility != null) {
            item(key = "abilities") {
                AbilitiesCard(
                    groups = info.abilityGroups,
                    selected = selectedAbility,
                    onSelect = { ability -> selectedAbilityName = ability.name },
                    changes = description.changes,
                )
            }
        }
        if (description.facets.isNotEmpty()) item(key = "facets") { FacetsCard(description.facets) }
        if (description.talents.isNotEmpty()) item(key = "talents") { TalentsCard(description.talents) }
        if (description.changes.isNotEmpty()) {
            item(key = "changes") { ChangesCard(description.changes, abilities.associate { it.name to it.imageUrl }) }
        }
        val wiki = description.wiki
        if (wiki.playstyle != null || wiki.tips != null) item(key = "strategy") { StrategyCard(wiki.playstyle, wiki.tips) }
        wiki.counters?.let { counters -> item(key = "counters") { CountersCard(counters) } }
        wiki.recommendedItems?.let { items -> item(key = "items") { RecommendedItemsCard(items) } }
        if (description.bio.isNotEmpty()) item(key = "bio") { BioCard(description.bio) }
        if (description.trivia.isNotEmpty()) item(key = "trivia") { TriviaCard(description.trivia) }
    }
}
