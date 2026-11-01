package n7.ad2.games.internal.games.mid.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.compose.EndActions
import n7.ad2.games.internal.compose.RecordPlate
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.room.GameMode

/** The result: last hits and denies of both sides, how accurate my hits were and how far the misses were */
@Composable
internal fun ColumnScope.MidEnd(state: MidViewModel.State, actions: MidActions) {
    val match = state.match
    val mine = match.score(state.mySide)
    val theirs = match.score(state.opponentSide)
    val title = when {
        state.training -> R.string.mid_end_training
        mine > theirs -> R.string.end_win
        mine < theirs -> R.string.end_lose
        else -> R.string.end_draw
    }
    Column(Modifier.weight(1f).padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PaperCard).padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            val myLabel = if (state.mode == GameMode.COOP) stringResource(R.string.mid_team, state.peerName.orEmpty()) else stringResource(R.string.mid_you)
            EndSide(state.myHero, myLabel, match.lastHits(state.mySide), match.denies(state.mySide), MeColor)
            if (!state.training) {
                Text(":", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                val opponentLabel = if (state.mode == GameMode.VERSUS) state.peerName.orEmpty() else state.botHero?.name.orEmpty()
                EndSide(state.opponentHero, opponentLabel, match.lastHits(state.opponentSide), match.denies(state.opponentSide), PeerColor)
            }
        }
        Text(stringResource(R.string.mid_gold, match.gold(state.mySide)), color = GoldColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        val stats = state.stats
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EndStat("${stats.accuracy}%", stringResource(R.string.mid_stat_accuracy), Modifier.weight(1f))
            EndStat(stats.early.toString(), stringResource(R.string.mid_stat_early), Modifier.weight(1f))
            EndStat(stats.late.toString(), stringResource(R.string.mid_stat_late), Modifier.weight(1f))
            EndStat(stringResource(R.string.mid_seconds, seconds(stats.averageErrorMs)), stringResource(R.string.mid_stat_error), Modifier.weight(1f))
        }
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = DimColor, fontSize = 13.sp)
        if (state.newRecord) RecordPlate(state.previousBest, now = mine)
    }
    Spacer(Modifier.padding(top = 12.dp))
    EndActions(listOfNotNull(state.peerName), actions.onAgain, actions.onOtherGame)
}

@Composable
private fun EndSide(hero: MidHero?, label: String, lastHits: Int, denies: Int, ring: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HeroAvatar(hero, ring, size = 52)
        Text(label, color = InkDim, fontSize = 12.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$lastHits", color = Ink, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(" / $denies", color = InkDim, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EndStat(value: String, label: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = AppTheme.color.textColor, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(label, color = DimColor, fontSize = 11.sp, maxLines = 1)
    }
}
