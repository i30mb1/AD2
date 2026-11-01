package n7.ad2.games.internal.games.show.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.EndActions
import n7.ad2.games.internal.compose.RecordPlate
import n7.ad2.games.internal.compose.avatarLetter
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowPlayer
import n7.ad2.games.internal.games.show.ShowRound
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.games.show.signed
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkBad
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.room.GameMode

/** The result: the places, how each of my rounds went, a record */
@Composable
internal fun ColumnScope.ShowEnd(state: ShowViewModel.State, actions: ShowActions) {
    val match = state.match
    val mine = match.score(state.meId)
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(stringResource(title(state)), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        val total = if (state.mode == GameMode.COOP) match.teamScore() else mine
        Text(stringResource(R.string.show_points, points(total)), color = ShowGold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        if (match.players.size > 1) Standings(state)
        Rounds(match, state.meId)
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = DimColor, fontSize = 13.sp)
        if (state.newRecord) RecordPlate(state.previousBest, now = total)
    }
    Spacer(Modifier.height(12.dp))
    EndActions(match.players.filter { it.id != state.meId }.map { it.name }, actions.onAgain, actions.onOtherGame)
}

private fun title(state: ShowViewModel.State): Int {
    val match = state.match
    if (state.mode != GameMode.VERSUS) return R.string.show_end_title
    val mine = match.score(state.meId)
    val best = match.players.filter { it.id != state.meId }.maxOfOrNull { match.score(it.id) } ?: return R.string.show_end_title
    return when {
        mine > best -> R.string.end_win
        mine < best -> R.string.end_lose
        else -> R.string.end_draw
    }
}

@Composable
private fun Standings(state: ShowViewModel.State) {
    val match = state.match
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        match.standings().forEachIndexed { place, player ->
            val color = PlayerColors[match.players.indexOf(player) % PlayerColors.size]
            StandingRow(place + 1, player.name, match.score(player.id), isMe = player.id == state.meId, color)
        }
    }
}

/** A place in a table: mine is a paper card with a gold edge */
@Composable
internal fun StandingRow(place: Int, name: String, score: Int, isMe: Boolean, color: Color) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isMe) PaperCard else AppTheme.color.surface)
            .then(if (isMe) Modifier.border(3.dp, ShowGold, shape) else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val textColor = if (isMe) Ink else AppTheme.color.textColor
        Text("$place", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Box(Modifier.size(28.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Text(avatarLetter(name), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(if (isMe) stringResource(R.string.show_you) else name, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(points(score), color = textColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** A cell per round: what it asked, right or not, the points */
@Composable
private fun Rounds(match: ShowMatch, meId: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        match.rounds.forEach { round ->
            val answer = match.answerOf(meId, round.index)
            RoundCell(round, answer, Modifier.weight(1f))
        }
    }
}

@Composable
internal fun RoundCell(round: ShowRound, answer: RoundAnswer?, modifier: Modifier) {
    val color = when {
        answer == null -> AppTheme.color.surface
        answer.correct -> InkGood
        answer.points < 0 -> InkBad
        else -> Color(0xFF5B5148)
    }
    Column(
        modifier = modifier.clip(RoundedCornerShape(8.dp)).background(color).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(stringResource(if (round.final) R.string.show_short_final else round.kind.short()), color = Color.White, fontSize = 10.sp, maxLines = 1)
        Text(answer?.let { signed(it.points) } ?: "—", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

private fun ShowKind.short(): Int = when (this) {
    ShowKind.MANA -> R.string.show_short_mana
    ShowKind.HERO -> R.string.show_short_hero
    ShowKind.VOICE -> R.string.show_short_voice
    ShowKind.COMPARE -> R.string.show_short_compare
    ShowKind.BUILD -> R.string.show_short_build
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ShowEndPreview() {
    val players = listOf(ShowPlayer("me", "Me"), ShowPlayer("p", "Pixel 8"))
    val rounds = ShowPlan.create(1)
    val answers = rounds.associate { round ->
        round.index to mapOf("me" to RoundAnswer(round.index % 3 != 1, if (round.index % 3 != 1) 150 + round.index * 10 else -100), "p" to RoundAnswer(true, 120))
    }
    val match = ShowMatch(players, rounds, round = rounds.lastIndex, answers = answers, isOver = true)
    AppTheme(isDarkTheme = true) {
        ShowScreen(ShowViewModel.State.start(GameMode.VERSUS, "me", match).copy(phase = ShowViewModel.State.Phase.END, newRecord = true, previousBest = 640), ShowActions(), clock = { 0 })
    }
}
