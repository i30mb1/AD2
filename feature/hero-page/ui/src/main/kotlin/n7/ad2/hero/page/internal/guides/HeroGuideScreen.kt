package n7.ad2.hero.page.internal.guides

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.hero.page.internal.info.components.InfoCard
import n7.ad2.hero.page.internal.info.components.InfoColors

private const val ITEM_IMAGE_RATIO = 88f / 64f
private const val ITEMS_IN_ROW = 4
private const val HERO_IMAGE_RATIO = 16f / 9f
private val goodColor = Color(0xFF81C784)
private val badColor = Color(0xFFFF8A80)

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
        contentPadding = PaddingValues(top = 12.dp, bottom = bottomInset + 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "stats") {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatTile(stringResource(R.string.guide_winrate), guide.winrate, Modifier.weight(1f))
                StatTile(stringResource(R.string.guide_matches), guide.matches, Modifier.weight(1f))
            }
        }
        if (guide.startingItems.isNotEmpty()) {
            item(key = "start") {
                InfoCard(title = stringResource(R.string.starting_items)) { ItemsGrid(guide.startingItems) }
            }
        }
        if (guide.phases.isNotEmpty()) {
            item(key = "phases") {
                InfoCard(title = stringResource(R.string.guide_items)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.phases.forEach { PhaseRow(it) }
                    }
                }
            }
        }
        if (guide.easyToWin.isNotEmpty() || guide.hardToWin.isNotEmpty()) {
            item(key = "matchups") {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MatchupColumn(stringResource(R.string.easy_to_win_heroes), guide.easyToWin, goodColor, Modifier.weight(1f))
                    MatchupColumn(stringResource(R.string.hard_to_win_heroes), guide.hardToWin, badColor, Modifier.weight(1f))
                }
            }
        }
        item(key = "source") {
            Text(
                text = stringResource(R.string.guide_source),
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(AppTheme.shape.large)
            .background(AppTheme.color.surface)
            .padding(12.dp),
    ) {
        Text(text = label.uppercase(), style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = AppTheme.color.textSecondaryColor)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
    }
}

// plain rows instead of FlowRow: FlowRow measures weight + aspectRatio children wrong and they overlap the title
@Composable
private fun ItemsGrid(items: List<VOGuideItem>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.chunked(ITEMS_IN_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { ItemImage(it, Modifier.weight(1f)) }
                repeat(ITEMS_IN_ROW - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun PhaseRow(phase: VOGuidePhase) {
    val (title, time, color) = when (phase.phase) {
        VOGuidePhase.Phase.EARLY -> Triple(R.string.guide_phase_early, R.string.guide_phase_early_time, goodColor)
        VOGuidePhase.Phase.MID -> Triple(R.string.guide_phase_mid, R.string.guide_phase_mid_time, Color(0xFFFFB74D))
        VOGuidePhase.Phase.LATE -> Triple(R.string.guide_phase_late, R.string.guide_phase_late_time, AppTheme.color.primary)
    }
    Row {
        Column(modifier = Modifier.width(64.dp).padding(top = 4.dp)) {
            Text(text = stringResource(title).uppercase(), style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = color)
            Text(text = stringResource(time), style = AppTheme.style.info.copy(fontSize = 10.sp), color = AppTheme.color.textSecondaryColor)
        }
        ItemsGrid(phase.items, Modifier.weight(1f))
    }
}

@Composable
private fun ItemImage(item: VOGuideItem, modifier: Modifier) {
    AsyncImage(
        model = item.imageUrl,
        contentDescription = item.itemName,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(ITEM_IMAGE_RATIO)
            .clip(AppTheme.shape.small)
            .background(AppTheme.color.background),
    )
}

@Composable
private fun MatchupColumn(title: String, heroes: List<VOGuideHero>, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(AppTheme.shape.large)
            .background(AppTheme.color.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = title.uppercase(), style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = color)
        heroes.forEach { hero ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(HERO_IMAGE_RATIO)
                        .clip(AppTheme.shape.small),
                ) {
                    AsyncImage(model = hero.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Text(
                        text = hero.heroName,
                        style = AppTheme.style.body.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(Color(0x73000000))
                            .padding(horizontal = 4.dp),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 50% is an even matchup, the bar shows how far from it the hero is
                    val distance = (kotlin.math.abs(hero.winrate - 50) / 10).toFloat().coerceIn(0.05f, 1f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(InfoColors.chip),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(distance)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(color),
                        )
                    }
                    Text(
                        text = hero.winrateText,
                        style = AppTheme.style.body,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
        }
    }
}
