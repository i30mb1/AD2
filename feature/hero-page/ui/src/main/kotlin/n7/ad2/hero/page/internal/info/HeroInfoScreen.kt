package n7.ad2.hero.page.internal.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.MatchupColumn
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.info.components.AbilitiesSection
import n7.ad2.hero.page.internal.info.components.ChangesSection
import n7.ad2.hero.page.internal.info.components.CountersSection
import n7.ad2.hero.page.internal.info.components.FacetsSection
import n7.ad2.hero.page.internal.info.components.InfoDivider
import n7.ad2.hero.page.internal.info.components.ItemsSection
import n7.ad2.hero.page.internal.info.components.LoreSection
import n7.ad2.hero.page.internal.info.components.StatsSection
import n7.ad2.hero.page.internal.info.components.StrategySection
import n7.ad2.hero.page.internal.info.components.SummaryCard
import n7.ad2.hero.page.internal.info.components.TalentsSection
import n7.ad2.hero.page.internal.info.domain.vo.InfoRow
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroSummary
import n7.ad2.hero.page.internal.info.domain.vo.rows
import n7.ad2.hero.page.internal.pager.foldsHeroBanner
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.TalentLevel

@Composable
internal fun HeroInfoScreen(viewModel: HeroInfoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        HeroInfoViewModel.State.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { LoadingAnimation() }

        is HeroInfoViewModel.State.Error -> ErrorScreen(error = s.error)
        is HeroInfoViewModel.State.Data -> HeroInfoContent(s.info, s.summary, s.guide)
    }
}

/**
 * Ordered by what matters before a game: the short card, then every ability with all its details (that is how
 * people learn when and where to press), talents, and at the bottom rows for the rest, each opening in a sheet.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HeroInfoContent(info: VOHeroInfo, summary: VOHeroSummary?, guide: VOGuide?) {
    val rows = info.rows(hasMatchups = guide.hasMatchups())
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    var openedRowName by rememberSaveable { mutableStateOf<String?>(null) }
    val openedRow = rows.firstOrNull { it.name == openedRowName }

    val abilities = info.abilityGroups.flatMap { it.abilities }
    val defaultAbility = info.abilityGroups.firstOrNull { it.group == VOAbilityGroup.Group.MAIN }?.abilities?.firstOrNull() ?: abilities.firstOrNull()
    var selectedAbilityName by rememberSaveable { mutableStateOf(defaultAbility?.name) }
    val selectedAbility = abilities.firstOrNull { it.name == selectedAbilityName } ?: defaultAbility

    BackHandler(enabled = sheetState.isVisible) { scope.launch { sheetState.hide() } }
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = AppTheme.color.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = bottomInset + 16.dp),
            ) {
                if (openedRow != null) {
                    Heading(stringResource(openedRow.title))
                    RowContent(openedRow, info, guide)
                } else {
                    // the sheet layout needs some content even while hidden
                    Box(Modifier.height(1.dp))
                }
            }
        },
    ) {
        InfoPage(
            info = info,
            rows = rows,
            summary = summary,
            selectedAbility = selectedAbility,
            onSelectAbility = { ability -> selectedAbilityName = ability.name },
            onOpenRow = { row ->
                openedRowName = row.name
                scope.launch { sheetState.show() }
            },
        )
    }
}

@Composable
private fun InfoPage(
    info: VOHeroInfo,
    rows: List<InfoRow>,
    summary: VOHeroSummary?,
    selectedAbility: Ability?,
    onSelectAbility: (Ability) -> Unit,
    onOpenRow: (InfoRow) -> Unit,
) {
    val description = info.description
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .foldsHeroBanner()
            .verticalScroll(rememberScrollState())
            .padding(top = 14.dp, bottom = bottomInset + 16.dp),
    ) {
        if (summary != null) SummaryCard(summary)
        if (selectedAbility != null) {
            Heading(stringResource(R.string.info_abilities))
            AbilitiesSection(
                groups = info.abilityGroups,
                selected = selectedAbility,
                onSelect = onSelectAbility,
                changes = description.changes,
            )
        }
        if (description.talents.isNotEmpty()) {
            Heading(stringResource(R.string.info_talents))
            TalentsSection(description.talents)
        }
        InfoDivider(Modifier.padding(top = 20.dp))
        rows.forEach { row ->
            MoreRow(stringResource(row.title), row.hint(info)) { onOpenRow(row) }
            InfoDivider()
        }
    }
}

@Composable
private fun Heading(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = AppTheme.color.textColor,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

@Composable
private fun MoreRow(title: String, hint: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.textColor, modifier = Modifier.weight(1f))
        if (hint != null) {
            Text(
                text = hint,
                fontSize = 12.sp,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.color.textSecondaryColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun RowContent(row: InfoRow, info: VOHeroInfo, guide: VOGuide?) {
    val description = info.description
    when (row) {
        InfoRow.STATS -> StatsSection(description.stats)
        InfoRow.FACETS -> FacetsSection(description.facets)
        InfoRow.COUNTERS -> {
            // real matchups first: the RU wiki mostly has an unfilled template there
            if (guide.hasMatchups()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MatchupColumn(stringResource(R.string.easy_to_win_heroes), guide!!.easyToWin, prosColor, Modifier.weight(1f))
                    MatchupColumn(stringResource(R.string.hard_to_win_heroes), guide.hardToWin, consColor, Modifier.weight(1f))
                }
            }
            if (description.wiki.counters.isNotEmpty()) CountersSection(description.wiki.counters)
        }
        InfoRow.ITEMS -> ItemsSection(description.wiki.recommendedItems)
        InfoRow.TIPS -> StrategySection(description.wiki.playstyle, description.wiki.tips)
        InfoRow.CHANGES -> ChangesSection(description.changes)
        InfoRow.LORE -> LoreSection(description.bio, description.trivia)
    }
}

private fun VOGuide?.hasMatchups(): Boolean = this != null && (easyToWin.isNotEmpty() || hardToWin.isNotEmpty())

private val prosColor = Color(0xFF81C784)
private val consColor = Color(0xFFFF8A80)

private val InfoRow.title: Int
    get() = when (this) {
        InfoRow.STATS -> R.string.info_stats
        InfoRow.FACETS -> R.string.info_facets
        InfoRow.COUNTERS -> R.string.info_counters
        InfoRow.ITEMS -> R.string.info_items
        InfoRow.TIPS -> R.string.info_strategy
        InfoRow.CHANGES -> R.string.info_changes
        InfoRow.LORE -> R.string.info_lore
    }

/** A glimpse of what is inside, so it is clear whether the row is worth opening. */
private fun InfoRow.hint(info: VOHeroInfo): String? {
    val description = info.description
    return when (this) {
        InfoRow.STATS -> "HP ${description.stats.health.toInt()} · MP ${description.stats.mana.toInt()}"
        InfoRow.FACETS -> description.facets.joinToString(" · ") { it.name }
        InfoRow.ITEMS -> description.wiki.recommendedItems.flatMap { it.entries }.count { it.name != null }.takeIf { it > 0 }?.toString()
        InfoRow.CHANGES -> description.changes.firstOrNull()?.patch
        InfoRow.COUNTERS, InfoRow.TIPS, InfoRow.LORE -> null
    }
}

@Preview(heightDp = 1600)
@Composable
private fun InfoPagePreview() {
    val ability = Ability(name = "Blade Fury", description = "Spins with a blade, immune to effects and damaging enemies around.")
    val info = VOHeroInfo(
        heroImageUrl = "",
        description = HeroDescription(
            name = "Juggernaut",
            talents = listOf(TalentLevel(25, "+1s Omnislash", "40% lifesteal"), TalentLevel(20, "+15% crit", "+120 Blade Fury DPS")),
            bio = "bio",
            changes = listOf(PatchChange("7.41d", 0, emptyList(), emptyList(), emptyList())),
        ),
        abilityGroups = listOf(
            VOAbilityGroup(VOAbilityGroup.Group.INNATE, listOf(Ability(name = "Bladeform"))),
            VOAbilityGroup(VOAbilityGroup.Group.MAIN, listOf(ability, Ability(name = "Healing Ward"), Ability(name = "Blade Dance"), Ability(name = "Omnislash"))),
        ),
    )
    val summary = VOHeroSummary(
        pros = listOf("Farms early and ignores magic while spinning", "Ultimate wins one on one fights"),
        cons = listOf("Weak without items"),
        threats = listOf(VOGuideHero("Treant", "", 31.7, "31.7%"), VOGuideHero("Sniper", "", 39.2, "39.2%")),
        keyItems = listOf(VOGuideItem("bfury", ""), VOGuideItem("manta", "")),
    )
    AppTheme(isDarkTheme = true) {
        Box(Modifier.background(AppTheme.color.background)) {
            InfoPage(info, info.rows(), summary, selectedAbility = ability, onSelectAbility = {}, onOpenRow = {})
        }
    }
}
