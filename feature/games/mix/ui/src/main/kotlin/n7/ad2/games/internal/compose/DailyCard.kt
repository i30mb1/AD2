package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.games.show.DailyResult
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.games.show.compose.dayTitle
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperEdge

/** The challenge of the day above the games: play it, or today's result once it is played */
@Composable
internal fun DailyCard(daily: GamesViewModel.DailyCard, onClick: () -> Unit) {
    val resources = LocalContext.current.resources
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, PaperEdge, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.daily), color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(dayTitle(daily.date), color = InkDim, fontSize = 13.sp)
            }
            if (daily.streak > 0) {
                Text(
                    resources.getQuantityString(R.plurals.daily_streak_chip, daily.streak, daily.streak),
                    color = Ink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(ShowGold).padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
        Text(stringResource(R.string.daily_rules), color = InkDim, fontSize = 13.sp, lineHeight = 18.sp)
        val result = daily.result
        if (result == null) {
            Text(
                text = stringResource(R.string.daily_play),
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ShowGold).padding(12.dp),
            )
        } else {
            Text(
                text = stringResource(R.string.daily_done, result.correct, result.answers.size, points(result.points)),
                color = InkGood,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390)
@Composable
private fun DailyCardPreview() {
    val day = LocalDate.of(2026, 11, 1)
    AppTheme(isDarkTheme = true) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DailyCard(GamesViewModel.DailyCard(day, result = null, streak = 4), onClick = {})
            DailyCard(GamesViewModel.DailyCard(day, DailyResult(day, List(10) { RoundAnswer(it % 5 != 2, if (it % 5 != 2) 180 else 0) }), streak = 5), onClick = {})
        }
    }
}
