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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R

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

/** «Ещё раз» и «Другая игра» внизу итога; по сети кнопка называет соперника, а если их несколько - зовет всех [others] */
@Composable
internal fun EndActions(others: List<String>, onAgain: () -> Unit, onOtherGame: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = when (others.size) {
                0 -> stringResource(R.string.end_again)
                1 -> stringResource(R.string.end_again_with, others.single())
                else -> stringResource(R.string.end_again_all)
            },
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
