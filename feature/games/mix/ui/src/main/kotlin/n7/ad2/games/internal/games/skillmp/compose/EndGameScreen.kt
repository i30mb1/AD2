package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.Avatar
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.games.internal.games.skillmp.SpellMatch
import n7.ad2.games.room.GameMode

@Composable
internal fun EndGameScreen(state: SkillGameViewModel.State, onAgain: () -> Unit, onOtherGame: () -> Unit, modifier: Modifier = Modifier) {
    val match = state.match
    val peerName = state.peerName
    Column(modifier = modifier.fillMaxSize().padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (match.mode) {
            GameMode.SOLO -> {
                Text(stringResource(R.string.games_mode_solo), color = DimColor, fontSize = 13.sp)
                ScoreCard { Text(pluralStringResource(R.plurals.game_points, match.myScore, match.myScore), color = AppTheme.color.textColor, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
            }
            GameMode.VERSUS -> {
                val title = when {
                    match.myScore > match.peerScore -> R.string.end_win
                    match.myScore < match.peerScore -> R.string.end_lose
                    else -> R.string.end_draw
                }
                Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                ScoreCard {
                    Player(stringResource(R.string.game_me_letter), stringResource(R.string.game_you), MeColor, dim = match.myScore < match.peerScore)
                    Text("${match.myScore} : ${match.peerScore}", color = AppTheme.color.textColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Player(peerName.orEmpty(), peerName.orEmpty(), PeerColor, dim = match.peerScore < match.myScore)
                }
            }
            GameMode.COOP -> {
                Text(stringResource(R.string.games_mode_coop), color = DimColor, fontSize = 13.sp)
                ScoreCard { Text(stringResource(R.string.end_shared_score, match.myScore), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            }
        }
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = DimColor, fontSize = 13.sp)
        if (state.newRecord) RecordPlate(state.previousBest, now = match.myScore)
        Stats(state)
        Spacer(Modifier.weight(1f))
        EndActions(peerName, onAgain, onOtherGame)
    }
}

/** Верно, точность, средний ответ и лучшая серия - то, что есть после игры */
@Composable
private fun Stats(state: SkillGameViewModel.State) {
    if (state.answered == 0) return
    val stats = listOfNotNull(
        "${state.correct}/${state.answered}" to stringResource(R.string.end_stat_correct),
        "${state.correct * 100 / state.answered}%" to stringResource(R.string.end_stat_accuracy),
        state.avgAnswerMs?.let { stringResource(R.string.end_seconds, it / 1000f) to stringResource(R.string.end_stat_answer) },
        (state.bestStreak.toString() to stringResource(R.string.end_stat_streak)).takeIf { state.match.mode != GameMode.VERSUS },
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.forEach { (value, label) ->
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(value, color = AppTheme.color.textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(label, color = DimColor, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ScoreCard(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.surface)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) { content() }
}

@Composable
private fun Player(avatarName: String, label: String, color: androidx.compose.ui.graphics.Color, dim: Boolean) {
    Column(Modifier.alpha(if (dim) 0.7f else 1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Avatar(avatarName, color, 48.dp)
        Text(label, color = AppTheme.color.textColor, fontSize = 12.sp)
    }
}

/** Gold plate for a new record, with the previous best when there was one. */
@Composable
internal fun RecordPlate(previousBest: Int?, now: Int, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GoldColor.copy(alpha = 0.1f))
            .border(1.dp, GoldColor.copy(alpha = 0.35f), shape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("🏆", fontSize = 18.sp)
        Column {
            Text(stringResource(R.string.end_new_record), color = GoldColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            if (previousBest != null) Text(stringResource(R.string.end_record_was, previousBest, now), color = AppTheme.color.textSecondaryColor, fontSize = 12.sp)
        }
    }
}

/** «Ещё раз» и «Другая игра» внизу итога; вдвоём кнопка называет соперника */
@Composable
internal fun EndActions(peerName: String?, onAgain: () -> Unit, onOtherGame: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (peerName != null) stringResource(R.string.end_again_with, peerName) else stringResource(R.string.end_again),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MeColor).clickable(onClick = onAgain).padding(15.dp),
        )
        Text(
            text = stringResource(R.string.end_other_game),
            color = AppTheme.color.textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.dp, LineColor, RoundedCornerShape(12.dp))
                .clickable(onClick = onOtherGame).padding(13.dp),
        )
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EndGameVersusPreview() {
    AppTheme(isDarkTheme = true) {
        val match = SpellMatch.start(GameMode.VERSUS).copy(myScore = 7, peerScore = 3, isOver = true)
        val state = SkillGameViewModel.State.init(match, peerName = "Pixel 8").copy(isEndGame = true, newRecord = true, previousBest = 5, correct = 8, answered = 10, avgAnswerMs = 2100)
        EndGameScreen(state, onAgain = {}, onOtherGame = {}, modifier = Modifier.padding(16.dp))
    }
}
