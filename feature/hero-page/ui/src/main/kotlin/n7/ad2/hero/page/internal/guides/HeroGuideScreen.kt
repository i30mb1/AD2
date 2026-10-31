package n7.ad2.hero.page.internal.guides

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import n7.ad2.hero.page.internal.pager.foldsHeroBanner
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideBar
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.hero.page.internal.guides.domain.vo.VOSkillBuild
import n7.ad2.hero.page.internal.guides.domain.vo.VOSkillRow
import n7.ad2.hero.page.internal.info.components.InfoColors

private val goodColor = Color(0xFF81C784)
private val badColor = Color(0xFFFF8A80)
private val midColor = Color(0xFFFFB74D)
private val lateColor = Color(0xFF82B1FF)
private val talentColor = Color(0xFF8FD17F)

@Composable
internal fun HeroGuideScreen(viewModel: HeroGuideViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        HeroGuideViewModel.State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingAnimation() }
        HeroGuideViewModel.State.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.guide_empty), style = AppTheme.style.H5, color = AppTheme.color.textSecondaryColor, textAlign = TextAlign.Center)
        }
        is HeroGuideViewModel.State.Error -> ErrorScreen(error = s.error)
        is HeroGuideViewModel.State.Data -> GuideContent(s.guide)
    }
}

@Composable
private fun GuideContent(guide: VOGuide) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = Modifier.foldsHeroBanner(),
        contentPadding = PaddingValues(top = 12.dp, bottom = bottomInset + 16.dp),
    ) {
        item(key = "stats") { Summary(guide) }
        if (guide.rankWinrates.isNotEmpty()) {
            item(key = "ranks") {
                Section(stringResource(R.string.guide_rank_winrate), isCaption = true) { WinrateBars(guide.rankWinrates, AppTheme.color.primary) }
            }
        }
        guide.skillBuild?.let { build ->
            item(key = "skills") {
                Section(stringResource(R.string.guide_skill_build), hint = stringResource(R.string.guide_skill_build_hint)) { SkillBuildGrid(build) }
            }
        }
        if (guide.startingItems.isNotEmpty() || guide.phases.isNotEmpty()) {
            item(key = "items") {
                Section(stringResource(R.string.guide_items)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (guide.startingItems.isNotEmpty()) {
                            PhaseRow(stringResource(R.string.guide_phase_start), stringResource(R.string.guide_phase_start_time), AppTheme.color.textSecondaryColor, guide.startingItems)
                        }
                        guide.phases.forEach { phase ->
                            val (title, time, color) = when (phase.phase) {
                                VOGuidePhase.Phase.EARLY -> Triple(R.string.guide_phase_early, R.string.guide_phase_early_time, goodColor)
                                VOGuidePhase.Phase.MID -> Triple(R.string.guide_phase_mid, R.string.guide_phase_mid_time, midColor)
                                VOGuidePhase.Phase.LATE -> Triple(R.string.guide_phase_late, R.string.guide_phase_late_time, lateColor)
                            }
                            PhaseRow(stringResource(title), stringResource(time), color, phase.items)
                        }
                    }
                }
            }
        }
        if (guide.easyToWin.isNotEmpty() || guide.hardToWin.isNotEmpty()) {
            item(key = "matchups") {
                Section(stringResource(R.string.guide_matchups)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MatchupColumn(stringResource(R.string.easy_to_win_heroes), guide.easyToWin, goodColor, Modifier.weight(1f))
                        MatchupColumn(stringResource(R.string.hard_to_win_heroes), guide.hardToWin, badColor, Modifier.weight(1f))
                    }
                }
            }
        }
        if (guide.durationWinrates.isNotEmpty()) {
            item(key = "durations") {
                Section(stringResource(R.string.guide_duration_winrate), isCaption = true) { WinrateBars(guide.durationWinrates, goodColor) }
            }
        }
        item(key = "source") {
            Text(
                text = stringResource(R.string.guide_source),
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
            )
        }
    }
}

@Composable
private fun Summary(guide: VOGuide) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StatTile(stringResource(R.string.guide_winrate), guide.winrate, Modifier.weight(1f), valueColor = goodColor)
        StatTile(stringResource(R.string.guide_matches), guide.matches, Modifier.weight(1f))
        StatTile(stringResource(R.string.guide_pro_picks), guide.proPicks.toString(), Modifier.weight(1f), extra = guide.proWinrate)
        StatTile(stringResource(R.string.guide_bans), guide.proBans.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier, valueColor: Color = AppTheme.color.textColor, extra: String? = null) {
    Column(
        modifier = modifier
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .padding(8.dp),
    ) {
        Text(text = label.uppercase(), style = captionStyle, color = AppTheme.color.textSecondaryColor, maxLines = 1)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = valueColor, maxLines = 1)
            if (extra != null) {
                Text(text = extra, fontSize = 11.sp, color = AppTheme.color.textSecondaryColor, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
            }
        }
    }
}

/** Big titles for what people act on (skills, items, matchups), small captions for the charts around them. */
@Composable
private fun Section(title: String, hint: String? = null, isCaption: Boolean = false, content: @Composable () -> Unit) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (isCaption) {
                Text(text = title.uppercase(), style = captionStyle.copy(fontSize = 11.sp), color = AppTheme.color.textSecondaryColor)
            } else {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
            }
            if (hint != null) {
                Spacer(Modifier.weight(1f))
                Text(text = hint, fontSize = 11.sp, color = AppTheme.color.textSecondaryColor)
            }
        }
        Spacer(Modifier.height(if (isCaption) 6.dp else 10.dp))
        content()
    }
}

@Composable
private fun WinrateBars(bars: List<VOGuideBar>, color: Color) {
    val min = bars.minOf { it.winrate }
    val max = bars.maxOf { it.winrate }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        bars.forEach { bar ->
            // the bars only show the difference between groups, so they grow from the lowest one, not from zero
            val fraction = if (max > min) ((bar.winrate - min) / (max - min)).toFloat() else 1f
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "%.1f".format(bar.winrate), fontSize = 10.sp, color = AppTheme.color.textColor)
                Box(
                    Modifier
                        .padding(vertical = 3.dp)
                        .fillMaxWidth()
                        .height(6.dp + 30.dp * fraction)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
                Text(text = bar.label.text(), fontSize = 10.sp, color = AppTheme.color.textSecondaryColor, maxLines = 1)
            }
        }
    }
}

@Composable
private fun VOGuideBar.Label.text(): String = when (this) {
    is VOGuideBar.Label.Rank -> stringArrayResource(R.array.guide_ranks_short).getOrElse(rank - 1) { rank.toString() }
    is VOGuideBar.Label.Minutes -> when {
        from == 0 && to != null -> stringResource(R.string.guide_minutes_up_to, to)
        to == null -> stringResource(R.string.guide_minutes_from, from)
        else -> stringResource(R.string.guide_minutes_range, from, to)
    }
}

/** "ability x skill point" grid, the cells shrink to the screen width so the whole build is visible at once. */
@Composable
private fun SkillBuildGrid(build: VOSkillBuild) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val gap = 2.dp
        val icon = 24.dp
        val cell = ((maxWidth - icon - gap * (build.levels + 1)) / build.levels).coerceAtMost(24.dp)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                Spacer(Modifier.width(icon))
                repeat(build.levels) { index ->
                    Text(
                        text = "${index + 1}",
                        fontSize = 9.sp,
                        color = AppTheme.color.textSecondaryColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(cell),
                    )
                }
            }
            build.rows.forEach { row ->
                val color = when (row.kind) {
                    VOSkillRow.Kind.ABILITY -> AppTheme.color.primary
                    VOSkillRow.Kind.TALENT -> talentColor
                    VOSkillRow.Kind.ATTRIBUTES -> AppTheme.color.textSecondaryColor
                }
                Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
                    if (row.kind != VOSkillRow.Kind.ABILITY) {
                        val mark = if (row.kind == VOSkillRow.Kind.TALENT) stringResource(R.string.guide_talent_short) else "+"
                        Box(
                            modifier = Modifier
                                .size(icon)
                                .border(1.dp, color, RoundedCornerShape(5.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text(mark, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color) }
                    } else {
                        AsyncImage(
                            model = row.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(icon)
                                .clip(RoundedCornerShape(5.dp))
                                .background(AppTheme.color.surface),
                        )
                    }
                    repeat(build.levels) { index ->
                        val level = index + 1
                        val isTaken = level in row.levels
                        Box(
                            modifier = Modifier
                                .size(width = cell, height = 20.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isTaken) color else InfoColors.chip.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isTaken) Text("$level", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.background)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhaseRow(title: String, time: String, color: Color, items: List<VOGuideItem>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.width(64.dp)) {
            Text(text = title.uppercase(), style = captionStyle, fontWeight = FontWeight.Bold, color = color)
            Text(text = time, fontSize = 10.sp, color = AppTheme.color.textSecondaryColor)
        }
        // small fixed icons: the row is a list of items to remember, not artwork
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEach { item ->
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.itemName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 40.dp, height = 30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppTheme.color.surface),
                )
            }
        }
    }
}

@Composable
internal fun MatchupColumn(title: String, heroes: List<VOGuideHero>, color: Color, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title.uppercase(), style = captionStyle, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        heroes.forEach { hero ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = hero.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 36.dp, height = 22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppTheme.color.surface),
                )
                Text(
                    text = hero.heroName,
                    fontSize = 13.sp,
                    color = AppTheme.color.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
                Text(text = hero.winrateText, fontSize = 13.sp, color = color)
            }
        }
    }
}

private val captionStyle = TextStyle(fontSize = 10.sp, letterSpacing = 0.6.sp, fontWeight = FontWeight.Medium)

@Preview(heightDp = 1400)
@Composable
private fun GuideContentPreview() {
    val item = VOGuideItem("tango", "")
    val hero = VOGuideHero("Magnus", "", 73.7, "73.7%")
    AppTheme(isDarkTheme = true) {
        Box(Modifier.background(AppTheme.color.background)) {
            GuideContent(
                VOGuide(
                    winrate = "52.6%",
                    matches = "737K",
                    proPicks = 10,
                    proWinrate = "40%",
                    proBans = 2,
                    rankWinrates = listOf(51.7, 51.9, 52.3, 52.7, 53.1, 53.2, 52.4).mapIndexed { index, value -> VOGuideBar(VOGuideBar.Label.Rank(index + 1), value) },
                    durationWinrates = listOf(
                        VOGuideBar(VOGuideBar.Label.Minutes(0, 20), 49.0),
                        VOGuideBar(VOGuideBar.Label.Minutes(20, 30), 49.2),
                        VOGuideBar(VOGuideBar.Label.Minutes(30, 40), 50.0),
                        VOGuideBar(VOGuideBar.Label.Minutes(50, null), 55.0),
                    ),
                    skillBuild = VOSkillBuild(
                        16,
                        listOf(
                            VOSkillRow("", VOSkillRow.Kind.ABILITY, setOf(1, 3, 5, 7)),
                            VOSkillRow("", VOSkillRow.Kind.ABILITY, setOf(2, 4, 14, 15)),
                            VOSkillRow("", VOSkillRow.Kind.ABILITY, setOf(8, 9, 10, 12)),
                            VOSkillRow("", VOSkillRow.Kind.ABILITY, setOf(6, 13)),
                            VOSkillRow("", VOSkillRow.Kind.TALENT, setOf(11)),
                            VOSkillRow("", VOSkillRow.Kind.ATTRIBUTES, setOf(16)),
                        ),
                    ),
                    startingItems = List(5) { item },
                    phases = listOf(VOGuidePhase(VOGuidePhase.Phase.EARLY, List(4) { item }), VOGuidePhase(VOGuidePhase.Phase.LATE, List(6) { item })),
                    easyToWin = List(5) { hero },
                    hardToWin = List(5) { hero.copy(heroName = "Treant Protector", winrateText = "31.7%") },
                ),
            )
        }
    }
}
