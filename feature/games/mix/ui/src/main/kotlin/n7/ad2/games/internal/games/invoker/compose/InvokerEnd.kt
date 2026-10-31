package n7.ad2.games.internal.games.invoker.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import n7.ad2.games.internal.games.invoker.InvokerRun
import n7.ad2.games.internal.games.invoker.InvokerViewModel
import n7.ad2.games.internal.games.invoker.spellQueue
import n7.ad2.games.internal.games.show.compose.PlayerColors
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.games.show.compose.StandingRow
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.room.GameMode

/** The minute is over: the score, how many spells, the best streak and the misses; over the network both players */
@Composable
internal fun ColumnScope.InvokerEnd(state: InvokerViewModel.State, actions: InvokerActions) {
    val run = state.run
    val title = when {
        state.mode != GameMode.VERSUS -> R.string.invoker_end_time
        run.score > state.peerScore -> R.string.end_win
        run.score < state.peerScore -> R.string.end_lose
        else -> R.string.end_draw
    }
    val total = if (state.mode == GameMode.COOP) run.score + state.peerScore else run.score
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.show_points, points(total)), color = ShowGold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        val peer = state.peerName
        if (peer != null) {
            val you = stringResource(R.string.show_you)
            val rows = listOf(true to run.score, false to state.peerScore).sortedByDescending { it.second }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEachIndexed { place, (isMe, score) ->
                    StandingRow(place + 1, if (isMe) you else peer, score, isMe, PlayerColors[if (isMe) 0 else 1])
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EndStat(run.invoked.toString(), stringResource(R.string.invoker_stat_invoked), Modifier.weight(1f))
            EndStat(run.bestStreak.toString(), stringResource(R.string.invoker_stat_streak), Modifier.weight(1f))
            EndStat(run.mistakes.toString(), stringResource(R.string.invoker_stat_mistakes), Modifier.weight(1f))
        }
        if (state.peerLeft) Text(stringResource(R.string.end_peer_left), color = DimColor, fontSize = 13.sp)
        if (state.newRecord) RecordPlate(state.previousBest, now = total)
    }
    Spacer(Modifier.height(12.dp))
    EndActions(if (state.mode == GameMode.SOLO) null else state.peerName, actions.onAgain, actions.onOtherGame)
}

@Composable
private fun EndStat(value: String, label: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(PaperCard).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = InkDim, fontSize = 11.sp, maxLines = 1)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InvokerEndPreview() {
    val run = InvokerRun(spellQueue(1), score = 2_430, invoked = 19, bestStreak = 11, mistakes = 3)
    AppTheme(isDarkTheme = true) {
        InvokerScreen(
            InvokerViewModel.State.start(GameMode.VERSUS, run, peerName = "Pixel 8").copy(phase = InvokerViewModel.State.Phase.END, peerScore = 2_100, newRecord = true, previousBest = 1_980),
            elapsedMs = { 60_000 },
            countdown = { 0 },
            actions = InvokerActions(),
        )
    }
}
