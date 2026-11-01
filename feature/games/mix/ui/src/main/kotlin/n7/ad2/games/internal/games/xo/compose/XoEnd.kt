package n7.ad2.games.internal.games.xo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.EndActions
import n7.ad2.games.internal.compose.RecordPlate
import n7.ad2.games.internal.compose.XpLine
import n7.ad2.games.internal.games.xo.Mark
import n7.ad2.games.internal.games.xo.XoResult
import n7.ad2.games.internal.games.xo.XoViewModel
import n7.ad2.games.room.GameMode

/** The game is over: who won and how, wins in a row and the record */
@Composable
internal fun ColumnScope.XoEnd(state: XoViewModel.State, actions: XoActions) {
    val result = state.result ?: return
    val title = when (result.winner) {
        null -> R.string.end_draw
        state.myMark -> R.string.end_win
        else -> R.string.end_lose
    }
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(reason(state, result), color = XoLightDim, fontSize = 14.sp)
        if (state.streak > 0) Text(stringResource(R.string.xo_streak, state.streak), color = XoGold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        XpLine(state.xp)
        if (state.newRecord) RecordPlate(state.previousBest, now = state.streak)
    }
    EndActions(listOfNotNull(state.peerName), actions.onAgain, actions.onOtherGame)
}

@Composable
private fun reason(state: XoViewModel.State, result: XoResult): String = when (result.reason) {
    XoResult.Reason.LINE -> stringResource(R.string.xo_end_line)
    XoResult.Reason.BOARDS -> stringResource(R.string.xo_end_boards, state.game.taken(state.myMark), state.game.taken(state.myMark.other))
    XoResult.Reason.FULL -> stringResource(R.string.xo_end_full)
    XoResult.Reason.RESIGN -> stringResource(if (result.winner == state.myMark) R.string.xo_end_resigned_peer else R.string.xo_end_resigned_me)
    XoResult.Reason.AGREED -> stringResource(R.string.xo_end_agreed)
    XoResult.Reason.LEFT -> stringResource(R.string.end_peer_left)
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun XoEndPreview() {
    AppTheme(isDarkTheme = true) {
        XoScreen(
            XoViewModel.State(
                mode = GameMode.VERSUS,
                game = previewGame(),
                myMark = Mark.X,
                peerName = "Pixel 8",
                peerReady = true,
                result = XoResult(null, XoResult.Reason.AGREED),
            ),
            XoActions(),
        )
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun XoEndWinPreview() {
    AppTheme(isDarkTheme = true) {
        XoScreen(
            XoViewModel.State(
                mode = GameMode.VERSUS,
                game = previewGame(),
                myMark = Mark.X,
                peerName = "Pixel 8",
                peerReady = true,
                result = XoResult(Mark.X, XoResult.Reason.RESIGN),
                streak = 4,
                newRecord = true,
                previousBest = 3,
            ),
            XoActions(),
        )
    }
}
