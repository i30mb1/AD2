package n7.ad2.tournaments.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.tournaments.R
import n7.ad2.tournaments.internal.data.MatchStatus
import n7.ad2.tournaments.internal.data.MatchTeam
import n7.ad2.tournaments.internal.data.TournamentMatch

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
    var drawerPercent: Float by remember { mutableStateOf(0f) }
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sections = listOf(
        R.string.tournaments_live to matches.filter { it.status == MatchStatus.LIVE },
        R.string.tournaments_upcoming to matches.filter { it.status == MatchStatus.UPCOMING },
        R.string.tournaments_finished to matches.filter { it.status == MatchStatus.FINISHED },
    ).filter { (_, list) -> list.isNotEmpty() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 4.dp + insetsTop * drawerPercent, start = 4.dp, end = 4.dp, bottom = 8.dp + insetsBottom),
    ) {
        sections.forEach { (title, list) ->
            item(key = "title_$title") { SectionTitle(stringResource(title)) }
            items(list, key = { it.url }) { match -> MatchCard(match, onMatchClicked) }
        }
    }
    DisposableEffect(key1 = Unit) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = AppTheme.style.H5,
        color = AppTheme.color.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
    )
}

@Composable
private fun MatchCard(match: TournamentMatch, onMatchClicked: (TournamentMatch) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onMatchClicked(match) }
            .background(AppTheme.color.surface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier.size(16.dp),
                    model = match.tournamentLogoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    style = AppTheme.style.info,
                    color = AppTheme.color.textSecondaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = match.tournament,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Team(match.team1, modifier = Modifier.weight(1f))
                MatchCenter(match, modifier = Modifier.width(88.dp))
                Team(match.team2, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Team(team: MatchTeam, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            modifier = Modifier.size(40.dp),
            model = team.logoUrl,
            contentDescription = team.name,
            contentScale = ContentScale.Fit,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            style = AppTheme.style.body,
            color = AppTheme.color.textColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            text = team.name,
        )
    }
}

@Composable
private fun MatchCenter(match: TournamentMatch, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (match.status) {
            MatchStatus.LIVE -> Text(
                style = AppTheme.style.H5,
                color = AppTheme.color.primary,
                fontWeight = FontWeight.Bold,
                text = stringResource(R.string.tournaments_live_badge),
            )

            MatchStatus.FINISHED -> Text(
                style = AppTheme.style.H4,
                color = AppTheme.color.textColor,
                fontWeight = FontWeight.Bold,
                text = match.score.orEmpty(),
            )

            MatchStatus.UPCOMING -> Text(
                style = AppTheme.style.H5,
                color = AppTheme.color.textColor,
                text = stringResource(R.string.tournaments_versus),
            )
        }
        match.time?.let { time ->
            Text(
                modifier = Modifier.padding(top = 2.dp),
                style = AppTheme.style.info,
                color = AppTheme.color.textSecondaryColor,
                textAlign = TextAlign.Center,
                text = time,
            )
        }
    }
}
