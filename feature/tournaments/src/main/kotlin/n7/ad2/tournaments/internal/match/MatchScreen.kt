package n7.ad2.tournaments.internal.match

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.tournaments.R
import n7.ad2.tournaments.internal.data.MatchStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun MatchScreen(state: MatchViewModel.State, onRetryClicked: () -> Unit, onWatchClicked: (MatchDetails) -> Unit) {
    when (state) {
        is MatchViewModel.State.Loading -> LoadingScreen()
        is MatchViewModel.State.Error -> ErrorScreen(state.error, onRetryClicked)
        is MatchViewModel.State.Content -> MatchContent(state.details, onWatchClicked)
    }
}

@Composable
private fun MatchContent(details: MatchDetails, onWatchClicked: (MatchDetails) -> Unit) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = insetsTop + 8.dp, bottom = insetsBottom + 16.dp),
    ) {
        item { Header(details) }
        if (details.twitchChannel != null || details.streamUrl != null) {
            item {
                Button(modifier = Modifier.fillMaxWidth(), onClick = { onWatchClicked(details) }) {
                    Text(text = stringResource(R.string.tournaments_watch))
                }
            }
        }
        details.forecast?.let { forecast -> item { ForecastCard(details, forecast) } }
        if (details.maps.isNotEmpty()) {
            item { SectionTitle(stringResource(R.string.tournaments_games)) }
            items(details.maps, key = { "map_${it.number}" }) { map ->
                GameCard(map, details, isInitiallyExpanded = map == details.maps.last())
            }
        }
        item { SectionTitle(stringResource(R.string.tournaments_lineups)) }
        item { Lineup(details.left) }
        item { Lineup(details.right) }
        if (details.headToHead.isNotEmpty()) {
            item { SectionTitle(stringResource(R.string.tournaments_head_to_head)) }
            items(details.headToHead, key = { "past_${it.startSeconds}_${it.leftName}" }) { past -> PastMatchRow(past) }
        }
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
private fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(AppTheme.color.surface),
        content = content,
    )
}

@Composable
private fun Header(details: MatchDetails) {
    Card {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(modifier = Modifier.size(18.dp), model = details.tournamentLogoUrl, contentDescription = null)
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    style = AppTheme.style.body,
                    color = AppTheme.color.textColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = listOfNotNull(details.tournament, details.stage).joinToString(" · "),
                )
            }
            details.prize?.let { prize ->
                Text(
                    style = AppTheme.style.info,
                    color = AppTheme.color.textSecondaryColor,
                    text = stringResource(R.string.tournaments_prize, prize),
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                TeamColumn(details.left, Modifier.weight(1f))
                ScoreColumn(details, Modifier.width(96.dp))
                TeamColumn(details.right, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TeamColumn(team: DetailsTeam, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(modifier = Modifier.size(56.dp), model = team.logoUrl, contentDescription = team.name, contentScale = ContentScale.Fit)
        Text(
            modifier = Modifier.padding(top = 4.dp),
            style = AppTheme.style.H5,
            color = AppTheme.color.textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            text = team.name,
        )
        if (team.position != null && team.rating != null) {
            Text(
                style = AppTheme.style.info,
                color = AppTheme.color.textSecondaryColor,
                text = stringResource(R.string.tournaments_team_rating, team.position, team.rating),
            )
        }
    }
}

@Composable
private fun ScoreColumn(details: MatchDetails, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (details.status) {
            MatchStatus.UPCOMING -> Text(style = AppTheme.style.H4, color = AppTheme.color.textColor, fontWeight = FontWeight.Bold, text = stringResource(R.string.tournaments_versus))
            else -> Text(
                style = AppTheme.style.H3,
                color = AppTheme.color.textColor,
                fontWeight = FontWeight.Bold,
                text = "${details.scoreLeft} : ${details.scoreRight}",
            )
        }
        when (details.status) {
            MatchStatus.LIVE -> Text(style = AppTheme.style.H5, color = AppTheme.color.primary, fontWeight = FontWeight.Bold, text = stringResource(R.string.tournaments_live_badge))
            MatchStatus.FINISHED -> Text(style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_status_finished))
            MatchStatus.UPCOMING -> Unit
        }
        Text(
            style = AppTheme.style.info,
            color = AppTheme.color.textSecondaryColor,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.tournaments_best_of, details.bestOf),
        )
        Text(
            style = AppTheme.style.info,
            color = AppTheme.color.textSecondaryColor,
            textAlign = TextAlign.Center,
            text = formatDate(details.startSeconds),
        )
    }
}

@Composable
private fun ForecastCard(details: MatchDetails, forecast: Forecast) {
    Card {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_forecast))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                if (forecast.leftPercent > 0) Box(Modifier.weight(forecast.leftPercent.toFloat()).height(8.dp).background(AppTheme.color.primary))
                if (forecast.drawPercent > 0) Box(Modifier.weight(forecast.drawPercent.toFloat()).height(8.dp).background(AppTheme.color.textSecondaryColor))
                if (forecast.rightPercent > 0) Box(Modifier.weight(forecast.rightPercent.toFloat()).height(8.dp).background(AppTheme.color.textColor))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(style = AppTheme.style.info, color = AppTheme.color.textColor, text = "${details.left.name} ${forecast.leftPercent}%")
                if (forecast.drawPercent > 0) {
                    Text(style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = "${stringResource(R.string.tournaments_draw)} ${forecast.drawPercent}%")
                }
                Text(style = AppTheme.style.info, color = AppTheme.color.textColor, text = "${details.right.name} ${forecast.rightPercent}%")
            }
        }
    }
}

@Composable
private fun GameCard(map: GameMap, details: MatchDetails, isInitiallyExpanded: Boolean) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }
    Card {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        style = AppTheme.style.H5,
                        color = AppTheme.color.textColor,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.tournaments_game_number, map.number),
                    )
                    val winner = when (map.winnerIsLeft) {
                        true -> stringResource(R.string.tournaments_game_winner, details.left.name)
                        false -> stringResource(R.string.tournaments_game_winner, details.right.name)
                        null -> if (map.isFinished) "" else stringResource(R.string.tournaments_game_in_progress)
                    }
                    if (winner.isNotEmpty()) {
                        Text(style = AppTheme.style.info, color = if (map.isFinished) AppTheme.color.textSecondaryColor else AppTheme.color.primary, text = winner)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        style = AppTheme.style.H5,
                        color = AppTheme.color.textColor,
                        fontWeight = FontWeight.Bold,
                        text = "${map.killsLeft} : ${map.killsRight}",
                    )
                    if (map.durationSeconds > 0) {
                        Text(style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = formatDuration(map.durationSeconds))
                    }
                }
            }
            if (isExpanded) {
                if (map.draft.isNotEmpty()) {
                    Text(modifier = Modifier.padding(top = 8.dp), style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_draft))
                    DraftRow(map.draft.filter { it.isLeft })
                    DraftRow(map.draft.filter { !it.isLeft })
                }
                TeamPlayers(details.left.name, map.players.filter { it.isLeft })
                TeamPlayers(details.right.name, map.players.filter { !it.isLeft })
            }
        }
    }
}

@Composable
private fun DraftRow(heroes: List<DraftHero>) {
    if (heroes.isEmpty()) return
    // picks first, bans dimmed after them
    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (heroes.filter { it.isPick } + heroes.filter { !it.isPick }).forEach { hero ->
            AsyncImage(
                modifier = Modifier
                    .size(width = 40.dp, height = 24.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .alpha(if (hero.isPick) 1f else 0.4f),
                model = hero.iconUrl,
                contentDescription = hero.name,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun TeamPlayers(teamName: String, players: List<MapPlayer>) {
    if (players.isEmpty()) return
    Text(
        modifier = Modifier.padding(top = 10.dp),
        style = AppTheme.style.body,
        color = AppTheme.color.primary,
        fontWeight = FontWeight.Bold,
        text = teamName,
    )
    players.forEach { player ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .size(width = 48.dp, height = 28.dp)
                    .clip(RoundedCornerShape(3.dp)),
                model = player.heroIconUrl,
                contentDescription = player.heroName,
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                Text(
                    style = AppTheme.style.body,
                    color = AppTheme.color.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = "${player.nick} · ${player.kills}/${player.deaths}/${player.assists}",
                )
                Text(
                    style = AppTheme.style.info,
                    color = AppTheme.color.textSecondaryColor,
                    text = stringResource(R.string.tournaments_gpm_xpm, player.gpm, player.xpm),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                player.itemIconUrls.take(6).forEach { item ->
                    AsyncImage(
                        modifier = Modifier
                            .size(width = 26.dp, height = 20.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        model = item,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun Lineup(team: DetailsTeam) {
    Card {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(modifier = Modifier.size(24.dp), model = team.logoUrl, contentDescription = null)
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    style = AppTheme.style.H5,
                    color = AppTheme.color.textColor,
                    fontWeight = FontWeight.Bold,
                    text = team.name,
                )
            }
            team.players.forEach { player ->
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(modifier = Modifier.size(width = 20.dp, height = 14.dp), model = player.flagUrl, contentDescription = null, contentScale = ContentScale.Fit)
                    Text(
                        modifier = Modifier.padding(start = 8.dp).weight(1f),
                        style = AppTheme.style.body,
                        color = AppTheme.color.textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = if (player.isCaptain) "★ ${player.nick}" else player.nick,
                    )
                    player.leaderboard?.let { place ->
                        Text(modifier = Modifier.padding(end = 12.dp), style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = "#$place")
                    }
                    player.kda?.let { kda ->
                        Text(style = AppTheme.style.info, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_kda, "%.2f".format(Locale.US, kda)))
                    }
                }
            }
        }
    }
}

@Composable
private fun PastMatchRow(past: PastMatch) {
    Card {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                style = AppTheme.style.info,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = "${formatDate(past.startSeconds)} · ${past.tournament}",
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Text(style = AppTheme.style.body, color = AppTheme.color.textColor, maxLines = 1, overflow = TextOverflow.Ellipsis, text = past.leftName)
                    AsyncImage(modifier = Modifier.padding(start = 6.dp).size(24.dp), model = past.leftLogoUrl, contentDescription = null)
                }
                Text(
                    modifier = Modifier.width(64.dp),
                    style = AppTheme.style.H5,
                    color = AppTheme.color.textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    text = "${past.scoreLeft}:${past.scoreRight}",
                )
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(modifier = Modifier.padding(end = 6.dp).size(24.dp), model = past.rightLogoUrl, contentDescription = null)
                    Text(style = AppTheme.style.body, color = AppTheme.color.textColor, maxLines = 1, overflow = TextOverflow.Ellipsis, text = past.rightName)
                }
            }
        }
    }
}

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.getDefault()).withZone(ZoneId.systemDefault())

private fun formatDate(seconds: Long): String = if (seconds <= 0) "" else dateFormatter.format(Instant.ofEpochSecond(seconds))

private fun formatDuration(seconds: Int): String = "%d:%02d".format(Locale.US, seconds / 60, seconds % 60)
