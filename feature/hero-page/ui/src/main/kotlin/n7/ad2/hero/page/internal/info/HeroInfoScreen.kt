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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.hero.page.internal.info.components.AGHANIM_TAB
import n7.ad2.hero.page.internal.info.components.AbilityBar
import n7.ad2.hero.page.internal.info.components.AbilityPanel
import n7.ad2.hero.page.internal.info.components.AghanimPanel
import n7.ad2.hero.page.internal.info.components.ChangesSection
import n7.ad2.hero.page.internal.info.components.CountersSection
import n7.ad2.hero.page.internal.info.components.FacetsSection
import n7.ad2.hero.page.internal.info.components.HeroNumbersCard
import n7.ad2.hero.page.internal.info.components.InfoDivider
import n7.ad2.hero.page.internal.info.components.ItemsSection
import n7.ad2.hero.page.internal.info.components.KeyItemsCard
import n7.ad2.hero.page.internal.info.components.LoreSection
import n7.ad2.hero.page.internal.info.components.MatchupsCards
import n7.ad2.hero.page.internal.info.components.StrategySection
import n7.ad2.hero.page.internal.info.components.TalentsSection
import n7.ad2.hero.page.internal.info.domain.vo.InfoRow
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.about
import n7.ad2.hero.page.internal.info.domain.vo.aghanim
import n7.ad2.hero.page.internal.info.domain.vo.hotkeys
import n7.ad2.hero.page.internal.info.domain.vo.keyItems
import n7.ad2.hero.page.internal.info.domain.vo.rows
import n7.ad2.hero.page.internal.pager.foldsHeroBanner
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityBehavior
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AbilityParam
import n7.ad2.heroes.domain.model.DamageType
import n7.ad2.heroes.domain.model.Dispellable
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroAttributes
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.HeroStats
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote
import n7.ad2.heroes.domain.model.Role
import n7.ad2.heroes.domain.model.SpellImmunity
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
        is HeroInfoViewModel.State.Data -> HeroInfoContent(s.info, s.guide)
    }
}

/**
 * Laid out like the hero page of the game: the hero in numbers, the abilities one at a time with a separate entry for
 * Aghanim's Scepter and Shard, talents, then what OpenDota knows about matchups and items. Rarely needed parts are
 * rows at the bottom, each opening in a sheet.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HeroInfoContent(info: VOHeroInfo, guide: VOGuide?) {
    val rows = info.rows(hasMatchups = guide.hasMatchups())
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    var openedRowName by rememberSaveable { mutableStateOf<String?>(null) }
    // the matchups section opens the counters sheet too, without a row of its own
    val openedRow = InfoRow.entries.firstOrNull { it.name == openedRowName }

    var level by rememberSaveable { mutableIntStateOf(1) }
    val abilities = info.abilityGroups.flatMap { it.abilities }
    val defaultTab = info.abilityGroups.firstOrNull { it.group == VOAbilityGroup.Group.MAIN }?.abilities?.firstOrNull()?.name
        ?: abilities.firstOrNull()?.name
        ?: AGHANIM_TAB
    var selectedTab by rememberSaveable { mutableStateOf(defaultTab) }
    val selected = selectedTab.takeIf { tab -> tab == AGHANIM_TAB || abilities.any { it.name == tab } } ?: defaultTab

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
            guide = guide,
            rows = rows,
            level = level,
            onLevelChange = { level = it },
            selectedTab = selected,
            onSelectTab = { selectedTab = it },
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
    guide: VOGuide?,
    rows: List<InfoRow>,
    level: Int,
    onLevelChange: (Int) -> Unit,
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    onOpenRow: (InfoRow) -> Unit,
) {
    val description = info.description
    val aghanim = remember(description) { description.abilities.aghanim() }
    val hotkeys = remember(description) { description.abilities.hotkeys() }
    val keyItems = guide?.keyItems().orEmpty()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .foldsHeroBanner()
            .verticalScroll(rememberScrollState())
            .padding(top = 14.dp, bottom = bottomInset + 16.dp),
    ) {
        HeroNumbersCard(description, level, onLevelChange)

        Heading(stringResource(R.string.info_abilities))
        AbilityBar(info.abilityGroups, hotkeys, aghanim, selectedTab, onSelectTab)
        Box(Modifier.height(16.dp))
        if (selectedTab == AGHANIM_TAB) {
            AghanimPanel(aghanim, hotkeys)
        } else {
            info.abilityGroups.flatMap { it.abilities }.firstOrNull { it.name == selectedTab }?.let { ability ->
                AbilityPanel(ability, description.talents.about(ability), description.changes.of(ability))
            }
        }

        if (description.talents.isNotEmpty()) {
            Heading(stringResource(R.string.info_talents))
            TalentsSection(description.talents)
        }
        if (guide != null && guide.hasMatchups()) {
            Heading(stringResource(R.string.info_counters), source = OPENDOTA)
            MatchupsCards(guide.easyToWin, guide.hardToWin) { onOpenRow(InfoRow.COUNTERS) }
        }
        if (keyItems.isNotEmpty()) {
            Heading(stringResource(R.string.info_items_title), source = OPENDOTA)
            KeyItemsCard(keyItems)
        }
        if (rows.isNotEmpty()) {
            InfoDivider(Modifier.padding(top = 22.dp))
            rows.forEach { row ->
                MoreRow(stringResource(row.title), row.hint(info)) { onOpenRow(row) }
                InfoDivider()
            }
        }
    }
}

private fun List<PatchChange>.of(ability: Ability): List<Pair<PatchChange, List<PatchNote>>> = mapNotNull { change ->
    change.abilities.firstOrNull { it.abilityName == ability.name }?.let { change to it.notes }
}

@Composable
private fun Heading(text: String, source: String? = null) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor, modifier = Modifier.weight(1f))
        if (source != null) Text(source, fontSize = 11.sp, color = AppTheme.color.textSecondaryColor)
    }
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
        InfoRow.FACETS -> FacetsSection(description.facets)
        InfoRow.COUNTERS -> {
            // real matchups first: the RU wiki mostly has an unfilled template there
            if (guide != null && guide.hasMatchups()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MatchupColumn(stringResource(R.string.easy_to_win_heroes), guide.easyToWin, prosColor, Modifier.weight(1f))
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

private const val OPENDOTA = "OpenDota"
private val prosColor = Color(0xFF81C784)
private val consColor = Color(0xFFFF8A80)

private val InfoRow.title: Int
    get() = when (this) {
        InfoRow.FACETS -> R.string.info_facets
        InfoRow.TIPS -> R.string.info_strategy
        InfoRow.ITEMS -> R.string.info_items
        InfoRow.COUNTERS -> R.string.info_counters
        InfoRow.CHANGES -> R.string.info_changes
        InfoRow.LORE -> R.string.info_lore
    }

/** A glimpse of what is inside, so it is clear whether the row is worth opening. */
private fun InfoRow.hint(info: VOHeroInfo): String? {
    val description = info.description
    return when (this) {
        InfoRow.FACETS -> description.facets.joinToString(" · ") { it.name }
        InfoRow.ITEMS -> description.wiki.recommendedItems.flatMap { it.entries }.count { it.name != null }.takeIf { it > 0 }?.toString()
        InfoRow.CHANGES -> description.changes.firstOrNull()?.patch
        InfoRow.TIPS, InfoRow.COUNTERS, InfoRow.LORE -> null
    }
}

private val previewBladeFury = Ability(
    name = "Blade Fury",
    kind = AbilityKind.BASIC,
    description = "Causes a bladestorm of destructive force around Juggernaut, rendering him immune to magic and dealing damage to nearby enemy units.",
    notes = listOf("Items can be used during Blade Fury."),
    behavior = listOf(AbilityBehavior.NO_TARGET),
    damageType = DamageType.MAGICAL,
    spellImmunity = SpellImmunity.NO,
    dispellable = Dispellable.NO,
    cooldown = "30/26/22/18",
    mana = "110",
    params = listOf(AbilityParam("DAMAGE PER SECOND", "85/115/145/175", talent = "+120"), AbilityParam("RADIUS", "260"), AbilityParam("DURATION", "5")),
)
private val previewHealingWard = Ability(
    name = "Healing Ward",
    kind = AbilityKind.BASIC,
    shard = "Increases ward heal and health.",
    params = listOf(AbilityParam("MAX HEALTH AS HEAL", "2%/3%/4%/5%", shard = "1.5%"), AbilityParam("ATTACKS TO DESTROY", "1", shard = "1")),
)
private val previewHero = HeroDescription(
    name = "Juggernaut",
    primaryAttribute = HeroAttribute.AGILITY,
    short = "Slices through foes with deadly precision",
    roles = listOf(HeroRole(Role.CARRY, 2), HeroRole(Role.ESCAPE, 1), HeroRole(Role.PUSHER, 1)),
    attributes = HeroAttributes(20.0, 2.0, 32.0, 2.8, 14.0, 1.4),
    stats = HeroStats(560.0, 2.5, 243.0, 0.7, 5.3, 26.4, 54.0, 56.0, 1.4, 150.0, 0.0, 305.0, 0.6, 1800.0, 800.0),
    talents = listOf(
        TalentLevel(25, "40% Blade Dance lifesteal", "+1s Omnislash duration"),
        TalentLevel(20, "+120 Blade Fury DPS", "+15% Blade Dance crit"),
    ),
    abilities = listOf(
        previewBladeFury,
        previewHealingWard,
        Ability(name = "Blade Dance", kind = AbilityKind.BASIC),
        Ability(name = "Swiftslash", kind = AbilityKind.SCEPTER, description = "Performs a short Omnislash for 1 second.", cooldown = "25", mana = "150"),
        Ability(name = "Bladeform", kind = AbilityKind.INNATE),
        Ability(name = "Omnislash", kind = AbilityKind.ULTIMATE),
    ),
    bio = "bio",
    changes = listOf(PatchChange("7.41d", 0, emptyList(), emptyList(), emptyList())),
)
private val previewInfo = VOHeroInfo(
    heroImageUrl = "",
    description = previewHero,
    abilityGroups = listOf(
        VOAbilityGroup(VOAbilityGroup.Group.INNATE, previewHero.abilities.filter { it.kind == AbilityKind.INNATE }),
        VOAbilityGroup(VOAbilityGroup.Group.MAIN, previewHero.abilities.filter { it.kind == AbilityKind.BASIC || it.kind == AbilityKind.ULTIMATE }),
    ),
)

@Composable
private fun InfoPagePreview(selectedTab: String, level: Int) {
    val hero = VOGuideHero("Magnus", "", 73.7, "73.7%")
    val guide = VOGuide(
        winrate = "",
        matches = "",
        proPicks = 0,
        proWinrate = null,
        proBans = 0,
        rankWinrates = emptyList(),
        durationWinrates = emptyList(),
        skillBuild = null,
        startingItems = emptyList(),
        phases = listOf(VOGuidePhase(VOGuidePhase.Phase.MID, List(4) { VOGuideItem("item", "") })),
        easyToWin = List(5) { hero },
        hardToWin = List(5) { hero.copy(winrate = 31.7) },
    )
    AppTheme(isDarkTheme = true) {
        Box(Modifier.background(AppTheme.color.background)) {
            InfoPage(
                info = previewInfo,
                guide = guide,
                rows = previewInfo.rows(hasMatchups = true),
                level = level,
                onLevelChange = {},
                selectedTab = selectedTab,
                onSelectTab = {},
                onOpenRow = {},
            )
        }
    }
}

@Preview(heightDp = 1700)
@Composable
private fun InfoPageAbilityPreview() = InfoPagePreview(selectedTab = previewBladeFury.name, level = 1)

@Preview(heightDp = 1100)
@Composable
private fun InfoPageAghanimPreview() = InfoPagePreview(selectedTab = AGHANIM_TAB, level = 25)
