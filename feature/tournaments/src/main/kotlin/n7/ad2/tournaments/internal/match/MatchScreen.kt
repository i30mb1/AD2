package n7.ad2.tournaments.internal.match

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.tournaments.R
import n7.ad2.tournaments.internal.SectionTitle
import n7.ad2.tournaments.internal.data.MatchStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val headerTopColor = Color(0xFF3B1F22)
private val dividerColor = Color(0xFF3A3D43)
private val forecastRightColor = Color(0xFF5B6170)
private const val BAN_ALPHA = 0.35f

@Composable
internal fun MatchScreen(state: MatchViewModel.State, onBackClicked: () -> Unit, onRetryClicked: () -> Unit, onWatchClicked: (MatchDetails) -> Unit) {
    when (state) {
        is MatchViewModel.State.Loading -> LoadingScreen()
        is MatchViewModel.State.Error -> ErrorScreen(state.error, onRetryClicked)
        is MatchViewModel.State.Content -> MatchContent(state.details, onBackClicked, onWatchClicked)
    }
}

@Composable
private fun MatchContent(details: MatchDetails, onBackClicked: () -> Unit, onWatchClicked: (MatchDetails) -> Unit) {
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = insetsBottom + 16.dp),
    ) {
        item(key = "header") { Header(details, onBackClicked, onWatchClicked) }
        details.forecast?.let { forecast -> item(key = "forecast") { ForecastCard(details, forecast, Modifier.padding(horizontal = 12.dp)) } }
        if (details.maps.isNotEmpty()) {
            item(key = "games") { SectionTitle(stringResource(R.string.tournaments_games), Modifier.padding(horizontal = 12.dp)) }
            itemsIndexed(details.maps, key = { _, map -> "map_${map.number}" }) { index, map ->
                GameCard(map, details, isInitiallyExpanded = index == details.maps.lastIndex, modifier = Modifier.padding(horizontal = 12.dp))
            }
        }
        item(key = "lineups") {
            Column(modifier = Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(stringResource(R.string.tournaments_lineups))
                Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Lineup(details.left, Modifier.weight(1f))
                    Lineup(details.right, Modifier.weight(1f))
                }
            }
        }
        if (details.headToHead.isNotEmpty()) {
            item(key = "past") {
                Column(modifier = Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.tournaments_head_to_head))
                    PastMatches(details)
                }
            }
        }
    }
}

@Composable
private fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface),
    ) {
        content()
    }
}

@Composable
private fun Header(details: MatchDetails, onBackClicked: () -> Unit, onWatchClicked: (MatchDetails) -> Unit) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(headerTopColor, AppTheme.color.background)))
            .padding(top = insetsTop),
    ) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.desc_back),
                tint = AppTheme.color.textColor,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onBackClicked)
                    .padding(10.dp),
            )
            AsyncImage(
                modifier = Modifier
                    .size(16.dp)
                    .clip(AppTheme.shape.small),
                model = details.tournamentLogoUrl,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp, end = 12.dp),
                fontSize = 13.sp,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = listOfNotNull(details.tournament, details.stage).joinToString(" · "),
            )
        }
        details.prize?.let { prize ->
            Text(
                modifier = Modifier.padding(start = 48.dp),
                fontSize = 11.sp,
                color = AppTheme.color.textSecondaryColor,
                text = stringResource(R.string.tournaments_prize, prize),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            TeamColumn(details.left, Modifier.weight(1f))
            ScoreColumn(details, Modifier.width(110.dp))
            TeamColumn(details.right, Modifier.weight(1f))
        }
        if (details.twitchChannel != null || details.streamUrl != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(AppTheme.color.primary)
                    .clickable(role = Role.Button) { onWatchClicked(details) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    style = AppTheme.style.H5,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    text = stringResource(R.string.tournaments_watch),
                )
            }
        }
    }
}

@Composable
private fun TeamColumn(team: DetailsTeam, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            model = team.logoUrl,
            contentDescription = team.name,
            contentScale = ContentScale.Fit,
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 15.sp,
            color = AppTheme.color.textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            text = team.name,
        )
        if (team.position != null && team.rating != null) {
            Text(
                fontSize = 11.sp,
                color = AppTheme.color.textSecondaryColor,
                text = stringResource(R.string.tournaments_team_rating, team.position, team.rating),
            )
        }
    }
}

@Composable
private fun ScoreColumn(details: MatchDetails, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            fontSize = 34.sp,
            color = AppTheme.color.textColor,
            fontWeight = FontWeight.Bold,
            text = if (details.status == MatchStatus.UPCOMING) stringResource(R.string.tournaments_versus) else "${details.scoreLeft} : ${details.scoreRight}",
        )
        val bestOf = stringResource(R.string.tournaments_bo, details.bestOf)
        when (details.status) {
            MatchStatus.LIVE -> Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.primary),
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    style = AppTheme.style.body,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.color.primary,
                    text = "${stringResource(R.string.tournaments_live_badge)} · $bestOf",
                )
            }

            MatchStatus.FINISHED -> Text(
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                text = "${stringResource(R.string.tournaments_status_finished)} · $bestOf",
            )

            MatchStatus.UPCOMING -> Text(style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor, text = bestOf)
        }
        Text(
            fontSize = 11.sp,
            color = AppTheme.color.textSecondaryColor,
            textAlign = TextAlign.Center,
            text = formatDate(details.startSeconds),
        )
    }
}

@Composable
private fun ForecastCard(details: MatchDetails, forecast: Forecast, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionTitle(stringResource(R.string.tournaments_forecast))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                if (forecast.leftPercent > 0) Box(Modifier.weight(forecast.leftPercent.toFloat()).height(8.dp).background(AppTheme.color.primary))
                if (forecast.drawPercent > 0) Box(Modifier.weight(forecast.drawPercent.toFloat()).height(8.dp).background(AppTheme.color.textSecondaryColor))
                if (forecast.rightPercent > 0) Box(Modifier.weight(forecast.rightPercent.toFloat()).height(8.dp).background(forecastRightColor))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ForecastLabel("${forecast.leftPercent}%", details.left.name, isPercentFirst = true)
                if (forecast.drawPercent > 0) ForecastLabel("${forecast.drawPercent}%", stringResource(R.string.tournaments_draw), isPercentFirst = true)
                ForecastLabel("${forecast.rightPercent}%", details.right.name, isPercentFirst = false)
            }
        }
    }
}

@Composable
private fun ForecastLabel(percent: String, name: String, isPercentFirst: Boolean) {
    Row {
        if (isPercentFirst) Text(style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor, text = "$percent ")
        Text(style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor, maxLines = 1, text = name)
        if (!isPercentFirst) Text(style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor, text = " $percent")
    }
}

@Composable
private fun GameCard(map: GameMap, details: MatchDetails, isInitiallyExpanded: Boolean, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }
    val arrowRotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "arrow")
    val isInProgress = !map.isFinished
    Card(modifier) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isInProgress) AppTheme.color.primary else AppTheme.color.textSecondaryColor.copy(alpha = 0.4f)),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                ) {
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
                        Text(style = AppTheme.style.body, color = if (isInProgress) AppTheme.color.primary else AppTheme.color.textSecondaryColor, text = winner)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        fontSize = 15.sp,
                        color = AppTheme.color.textColor,
                        fontWeight = FontWeight.Bold,
                        text = "${map.killsLeft} : ${map.killsRight}",
                    )
                    if (map.durationSeconds > 0) {
                        Text(fontSize = 11.sp, color = AppTheme.color.textSecondaryColor, text = formatDuration(map.durationSeconds))
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppTheme.color.textSecondaryColor,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .rotate(arrowRotation),
                )
            }
            if (isExpanded) {
                Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                    if (map.draft.isNotEmpty()) {
                        Text(fontSize = 11.sp, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_draft))
                        DraftRow(map.draft.filter { it.isLeft })
                        DraftRow(map.draft.filter { !it.isLeft })
                    }
                    TeamPlayers(details.left.name, map.players.filter { it.isLeft })
                    TeamPlayers(details.right.name, map.players.filter { !it.isLeft })
                }
            }
        }
    }
}

@Composable
private fun DraftRow(heroes: List<DraftHero>) {
    if (heroes.isEmpty()) return
    // picks first, bans dimmed after them
    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        (heroes.filter { it.isPick } + heroes.filter { !it.isPick }).forEach { hero ->
            AsyncImage(
                modifier = Modifier
                    .size(width = 40.dp, height = 22.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .alpha(if (hero.isPick) 1f else BAN_ALPHA),
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
        color = AppTheme.color.textSecondaryColor,
        fontWeight = FontWeight.Bold,
        text = teamName,
    )
    players.forEach { player ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(width = 44.dp, height = 25.dp)
                    .clip(RoundedCornerShape(3.dp)),
                model = player.heroIconUrl,
                contentDescription = player.heroName,
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
            ) {
                Text(fontSize = 13.sp, color = AppTheme.color.textColor, maxLines = 1, overflow = TextOverflow.Ellipsis, text = player.nick)
                Text(fontSize = 11.sp, color = AppTheme.color.textSecondaryColor, text = stringResource(R.string.tournaments_gpm_xpm, player.gpm, player.xpm))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
                player.itemIconUrls.take(6).forEach { item ->
                    AsyncImage(
                        modifier = Modifier
                            .size(width = 20.dp, height = 15.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        model = item,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Text(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textColor,
                text = "${player.kills}/${player.deaths}/${player.assists}",
            )
        }
    }
}

@Composable
private fun Lineup(team: DetailsTeam, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape),
                model = team.logoUrl,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 13.sp,
                color = AppTheme.color.textColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = team.name,
            )
        }
        team.players.forEach { player ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        modifier = Modifier.size(width = 16.dp, height = 11.dp),
                        model = player.flagUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        fontSize = 13.sp,
                        color = AppTheme.color.textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = player.nick,
                    )
                    if (player.isCaptain) Text(fontSize = 11.sp, color = AppTheme.color.primary, text = "★")
                }
                player.kda?.let { kda ->
                    Text(
                        modifier = Modifier.padding(start = 22.dp),
                        fontSize = 11.sp,
                        color = AppTheme.color.textSecondaryColor,
                        text = stringResource(R.string.tournaments_kda, "%.2f".format(Locale.US, kda)),
                    )
                }
            }
        }
    }
}

@Composable
private fun PastMatches(details: MatchDetails) {
    Card {
        Column {
            details.headToHead.forEachIndexed { index, past ->
                if (index > 0) Divider(color = dividerColor)
                // the chip is red when the left team of this match won that meeting
                val leftScore = if (past.leftName == details.left.name) past.scoreLeft else past.scoreRight
                val rightScore = if (past.leftName == details.left.name) past.scoreRight else past.scoreLeft
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            fontSize = 13.sp,
                            color = AppTheme.color.textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = "${past.leftName} — ${past.rightName}",
                        )
                        Text(
                            fontSize = 11.sp,
                            color = AppTheme.color.textSecondaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = listOf(past.tournament, formatDate(past.startSeconds)).filter { it.isNotEmpty() }.joinToString(" · "),
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .widthIn(min = 44.dp)
                            .clip(AppTheme.shape.small)
                            .background(if (leftScore > rightScore) AppTheme.color.primary.copy(alpha = 0.25f) else dividerColor)
                            .padding(vertical = 3.dp, horizontal = 6.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.textColor,
                        textAlign = TextAlign.Center,
                        text = "${past.scoreLeft}:${past.scoreRight}",
                    )
                }
            }
        }
    }
}

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.getDefault()).withZone(ZoneId.systemDefault())

private fun formatDate(seconds: Long): String = if (seconds <= 0) "" else dateFormatter.format(Instant.ofEpochSecond(seconds))

private fun formatDuration(seconds: Int): String = "%d:%02d".format(Locale.US, seconds / 60, seconds % 60)
