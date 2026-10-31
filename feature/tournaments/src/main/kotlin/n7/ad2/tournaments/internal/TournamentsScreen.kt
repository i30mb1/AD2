package n7.ad2.tournaments.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.FilterChip
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.core.ui.compose.view.ScrollToTopFab
import n7.ad2.feature.tournaments.R
import n7.ad2.tournaments.internal.data.MatchStatus
import n7.ad2.tournaments.internal.data.MatchTeam
import n7.ad2.tournaments.internal.data.TournamentMatch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val JUMP_INDEX = 20
private const val LOSER_ALPHA = 0.55f
private val todayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

@Composable
internal fun TournamentsScreen(
    state: TournamentsViewModel.State,
    drawerPercentListener: DrawerPercentListener,
    onMatchClicked: (TournamentMatch) -> Unit,
    onRetryClicked: () -> Unit,
) {
    when (state) {
        is TournamentsViewModel.State.Loading -> LoadingScreen()
        is TournamentsViewModel.State.Error -> ErrorScreen(state.error, onRetryClicked)
        is TournamentsViewModel.State.Content -> MatchesList(state.matches, drawerPercentListener, onMatchClicked)
    }
}

@Composable
private fun MatchesList(matches: List<TournamentMatch>, drawerPercentListener: DrawerPercentListener, onMatchClicked: (TournamentMatch) -> Unit) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var drawerPercent by remember { mutableFloatStateOf(0f) }
    // null shows every status
    var filter by rememberSaveable { mutableStateOf<MatchStatus?>(null) }
    val isFiltersScrolledAway by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sections = listOf(
        MatchStatus.LIVE to R.string.tournaments_live,
        MatchStatus.UPCOMING to R.string.tournaments_upcoming,
        MatchStatus.FINISHED to R.string.tournaments_finished,
    )
        .filter { (status, _) -> filter == null || filter == status }
        .map { (status, title) -> title to matches.filter { it.status == status } }
        .filter { (_, list) -> list.isNotEmpty() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 6.dp + insetsTop * drawerPercent,
                bottom = 16.dp + insetsBottom * drawerPercent,
            ),
        ) {
            item(key = "filters") {
                Filters(selected = filter, onSelect = { selected ->
                    filter = selected
                    scope.launch { state.scrollToItem(0) }
                })
            }
            sections.forEach { (title, list) ->
                item(key = "title_$title") { SectionTitle(stringResource(title)) }
                items(list, key = { it.url }) { match -> MatchCard(match, onMatchClicked) }
            }
        }
        ScrollToTopFab(
            isVisible = isFiltersScrolledAway,
            onClick = {
                scope.launch {
                    if (state.firstVisibleItemIndex > JUMP_INDEX) state.scrollToItem(JUMP_INDEX)
                    state.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
    DisposableEffect(drawerPercentListener) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun Filters(selected: MatchStatus?, onSelect: (MatchStatus?) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(stringResource(R.string.tournaments_filter_all), selected == null, onClick = { onSelect(null) })
        FilterChip(stringResource(R.string.tournaments_live_badge), selected == MatchStatus.LIVE, onClick = { onSelect(MatchStatus.LIVE) }, hasDot = true)
        FilterChip(stringResource(R.string.tournaments_upcoming), selected == MatchStatus.UPCOMING, onClick = { onSelect(MatchStatus.UPCOMING) })
        FilterChip(stringResource(R.string.tournaments_finished), selected == MatchStatus.FINISHED, onClick = { onSelect(MatchStatus.FINISHED) })
    }
}

@Composable
internal fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = AppTheme.style.body,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = AppTheme.color.textSecondaryColor,
        modifier = modifier.padding(start = 4.dp, top = 8.dp),
    )
}

@Composable
private fun MatchCard(match: TournamentMatch, onMatchClicked: (TournamentMatch) -> Unit, modifier: Modifier = Modifier) {
    // "2:0" -> the left team won, the loser is dimmed
    val scores = match.score?.split(':')?.mapNotNull { it.trim().toIntOrNull() }?.takeIf { it.size == 2 }
    val leftAlpha = if (scores != null && scores[0] < scores[1]) LOSER_ALPHA else 1f
    val rightAlpha = if (scores != null && scores[1] < scores[0]) LOSER_ALPHA else 1f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .clickable { onMatchClicked(match) },
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (match.status == MatchStatus.LIVE) AppTheme.color.primary else Color.Transparent),
        )
        Column(modifier = Modifier.padding(start = 9.dp, end = 12.dp, top = 10.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(AppTheme.shape.small),
                    model = match.tournamentLogoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 11.sp,
                    color = AppTheme.color.textSecondaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = match.tournament,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Team(match.team1, isWinner = rightAlpha < 1f, isLeft = true, modifier = Modifier.weight(1f).alpha(leftAlpha))
                MatchCenter(match, modifier = Modifier.width(84.dp))
                Team(match.team2, isWinner = leftAlpha < 1f, isLeft = false, modifier = Modifier.weight(1f).alpha(rightAlpha))
            }
        }
    }
}

@Composable
private fun Team(team: MatchTeam, isWinner: Boolean, isLeft: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val logo = @Composable {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                model = team.logoUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        }
        if (isLeft) logo()
        Text(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(horizontal = 8.dp),
            style = AppTheme.style.H5,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Medium,
            color = AppTheme.color.textColor,
            textAlign = if (isLeft) TextAlign.Start else TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = team.name,
        )
        if (!isLeft) logo()
    }
}

@Composable
private fun MatchCenter(match: TournamentMatch, modifier: Modifier = Modifier) {
    val isLive = match.status == MatchStatus.LIVE
    val (center, sub) = when (match.status) {
        MatchStatus.LIVE -> stringResource(R.string.tournaments_live_badge) to match.time.orEmpty()
        MatchStatus.FINISHED -> match.score.orEmpty().replace(":", " : ") to match.time.orEmpty()
        MatchStatus.UPCOMING -> upcomingTime(match.time)
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            fontSize = if (match.status == MatchStatus.UPCOMING) 17.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLive) AppTheme.color.primary else AppTheme.color.textColor,
            maxLines = 1,
            text = center,
        )
        if (sub.isNotEmpty()) {
            Text(
                fontSize = 11.sp,
                color = if (isLive) AppTheme.color.primary else AppTheme.color.textSecondaryColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = sub,
            )
        }
    }
}

/** "13:50 30.10" -> "13:50" over "today" or over the date. */
@Composable
private fun upcomingTime(time: String?): Pair<String, String> {
    val parts = time.orEmpty().split(' ').filter { it.isNotBlank() }
    val hours = parts.getOrNull(0) ?: return stringResource(R.string.tournaments_versus) to ""
    val date = parts.getOrNull(1).orEmpty()
    val isToday = date == LocalDate.now().format(todayFormatter)
    return hours to if (isToday) stringResource(R.string.tournaments_today) else date
}

@Preview
@Composable
private fun MatchCardPreview() {
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MatchCard(TournamentMatch("1", "BetBoom Dacha Dubai 2026", "", MatchTeam("Team Spirit", ""), MatchTeam("Tundra", ""), MatchStatus.LIVE, null, "Игра 2 · 34:12"), {})
            MatchCard(TournamentMatch("2", "PGL Wallachia S6", "", MatchTeam("Aurora", ""), MatchTeam("Team Liquid", ""), MatchStatus.FINISHED, "1:2", "5 час. назад"), {})
            MatchCard(TournamentMatch("3", "FISSURE Universe S7", "", MatchTeam("Xtreme Gaming", ""), MatchTeam("Gaimin Gladiators", ""), MatchStatus.UPCOMING, null, "13:00 31.10"), {})
        }
    }
}
