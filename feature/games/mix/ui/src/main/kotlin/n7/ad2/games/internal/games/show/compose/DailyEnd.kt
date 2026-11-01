package n7.ad2.games.internal.games.show.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.compose.XpLine
import n7.ad2.games.internal.games.show.DailyPlan
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowPlayer
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode

/** The result of the day: how many right, the points, a cell per question, the streak and who nearby played it today */
@Composable
internal fun ColumnScope.DailyEnd(state: ShowViewModel.State, actions: ShowActions) {
    val match = state.match
    val answers = match.rounds.map { match.answerOf(state.meId, it.index) }
    val correct = answers.count { it?.correct == true }
    val total = answers.sumOf { it?.points ?: 0 }
    val resources = LocalContext.current.resources
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column {
            Text(stringResource(R.string.daily), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(dayTitle(state.date), color = DimColor, fontSize = 14.sp)
        }
        PaperPanel {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                BigNumber("$correct", stringResource(R.string.daily_of, match.rounds.size))
                BigNumber(points(total), resources.getQuantityString(R.plurals.daily_points, total.absoluteValue))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            match.rounds.chunked(5).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { round -> RoundCell(round, answers[round.index], Modifier.weight(1f)) }
                }
            }
        }
        if (state.streak > 0) Text(resources.getQuantityString(R.plurals.daily_streak, state.streak, state.streak), color = AppTheme.color.textColor, fontSize = 14.sp)
        XpLine(state.xp)
        Text(stringResource(R.string.daily_nearby), color = DimColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        if (state.nearby.none { it.playerId != state.meId }) {
            Text(stringResource(R.string.daily_nearby_empty), color = AppTheme.color.textSecondaryColor, fontSize = 13.sp, lineHeight = 18.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.nearby.forEachIndexed { place, record ->
                    StandingRow(place + 1, record.playerName, record.score, isMe = record.playerId == state.meId, PlayerColors[place % PlayerColors.size])
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val shape = RoundedCornerShape(12.dp)
        Text(
            text = stringResource(R.string.daily_share),
            color = Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).clip(shape).background(ShowGold).clickable(role = Role.Button, onClick = actions.onShare).padding(15.dp),
        )
        Text(
            text = stringResource(R.string.daily_to_games),
            color = AppTheme.color.textColor,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).clip(shape).border(1.dp, LineColor, shape).clickable(role = Role.Button, onClick = actions.onOtherGame).padding(15.dp),
        )
    }
}

/** «1 ноября» in the language of the phone */
@Composable
internal fun dayTitle(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("d MMMM", LocalConfiguration.current.locales[0]))

@Composable
private fun BigNumber(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Ink, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = InkDim, fontSize = 13.sp)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DailyEndPreview() {
    val rounds = DailyPlan.create()
    val answers = rounds.associate { round -> round.index to mapOf("me" to RoundAnswer(round.index % 4 != 3, if (round.index % 4 != 3) 170 else 0)) }
    val match = ShowMatch(listOf(ShowPlayer("me", "Me")), rounds, round = rounds.lastIndex, answers = answers, isOver = true)
    val nearby = listOf(Record("g", "Galaxy", "daily", GameMode.SOLO, 1_900, 0), Record("me", "Me", "daily", GameMode.SOLO, 1_360, 0), Record("p", "Pixel 8", "daily", GameMode.SOLO, 1_120, 0))
    AppTheme(isDarkTheme = true) {
        ShowScreen(
            ShowViewModel.State.start(GameMode.SOLO, "me", match, daily = true, date = LocalDate.of(2026, 11, 1)).copy(phase = ShowViewModel.State.Phase.END, streak = 5, nearby = nearby),
            ShowActions(),
            clock = { 0 },
        )
    }
}
