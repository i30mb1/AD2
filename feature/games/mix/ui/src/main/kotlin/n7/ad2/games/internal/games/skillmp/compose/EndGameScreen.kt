package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.skillmp.SpellMatch
import n7.ad2.games.room.GameMode

@Composable
internal fun EndGameScreen(match: SpellMatch, peerName: String?, newRecord: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        when (match.mode) {
            GameMode.SOLO -> Counter(match.myScore)
            GameMode.VERSUS -> {
                val title = when {
                    match.myScore > match.peerScore -> R.string.end_win
                    match.myScore < match.peerScore -> R.string.end_lose
                    else -> R.string.end_draw
                }
                Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("${match.myScore} : ${match.peerScore}", color = AppTheme.color.textColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                peerName?.let { Text(it, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body) }
            }
            GameMode.COOP -> Text(stringResource(R.string.end_shared_score, match.myScore), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
        if (newRecord) Text(stringResource(R.string.end_new_record), color = Color(0xFFFFD166), fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}
