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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.show.compose.PlayerColors
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.games.show.compose.StandingRow
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.leaderboard.FriendsRecords
import n7.ad2.games.internal.leaderboard.SavedRecord
import n7.ad2.games.internal.progress.XpGain
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode

/** The record after a game: a solo game among friends shows their table, otherwise a new record gets the gold plate */
@Composable
internal fun EndRecord(record: SavedRecord, mode: GameMode, modifier: Modifier = Modifier) {
    when {
        mode == GameMode.SOLO && record.friends.others.isNotEmpty() -> FriendsTable(record, modifier)
        record.isNew -> RecordPlate(record.previousBest, now = record.score, modifier)
    }
}

/** The records of the friends met over the network: the first three and me, my new one marked, who it passed and how far the place above is */
@Composable
private fun FriendsTable(record: SavedRecord, modifier: Modifier) {
    val friends = record.friends
    Column(modifier.fillMaxWidth().padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.records_friends), color = DimColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.games_chip_solo), color = DimColor, fontSize = 12.sp)
        }
        friends.table.forEach { (index, row) ->
            val isNew = record.isNew && row.playerId == friends.meId
            StandingRow(
                place = index + 1,
                name = row.playerName,
                score = row.score,
                isMe = row.playerId == friends.meId,
                color = PlayerColors[index % PlayerColors.size],
                tag = if (isNew) stringResource(R.string.records_new) else null,
                note = record.previousBest?.takeIf { isNew }?.let { stringResource(R.string.records_was, points(it)) },
            )
        }
        FriendsLine(record)
    }
}

/** «Обогнали Pixel 8. До 1-го места не хватает 210.»: who the record passed and the place above, or the first place */
@Composable
private fun FriendsLine(record: SavedRecord) {
    val friends = record.friends
    val place = friends.place
    val behind = friends.behind
    val passed = record.passed.takeIf { it.isNotEmpty() }?.let { players -> stringResource(R.string.records_passed, players.joinToString { it.playerName }) }
    val above = when {
        place == 1 -> stringResource(R.string.records_first_among)
        place != null && behind != null -> stringResource(R.string.records_to_place, place - 1, points(behind))
        else -> null
    }
    val line = listOfNotNull(passed, above).joinToString(" ")
    if (line.isEmpty()) return
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (passed != null) Text("↑", color = ShowGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Text(line, color = AppTheme.color.textSecondaryColor, fontSize = 13.sp, lineHeight = 18.sp)
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

/** Что дала игра: «+50 опыта», а с новым уровнем - и его, золотом */
@Composable
internal fun XpLine(xp: XpGain?, modifier: Modifier = Modifier) {
    if (xp == null) return
    val level = xp.newLevel
    Text(
        text = if (level != null) stringResource(R.string.end_xp_level, xp.points, level) else stringResource(R.string.end_xp, xp.points),
        color = if (level != null) GoldColor else AppTheme.color.textSecondaryColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
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

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390)
@Composable
private fun EndPartsPreview() {
    val friends = FriendsRecords(
        listOf(
            Record("g", "Galaxy", "invoker", GameMode.SOLO, 2_640, 0),
            Record("me", "Mi A2", "invoker", GameMode.SOLO, 2_430, 0),
            Record("p", "Pixel 8", "invoker", GameMode.SOLO, 2_100, 0),
            Record("r", "Redmi", "invoker", GameMode.SOLO, 900, 0),
        ),
        meId = "me",
    )
    AppTheme(isDarkTheme = true) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EndRecord(SavedRecord(2_430, previousBest = 1_980, isNew = true, friends = friends, passed = listOf(friends.records[2])), GameMode.SOLO)
            XpLine(XpGain(50, newLevel = null))
            XpLine(XpGain(50, newLevel = 8))
            RecordPlate(previousBest = 3, now = 4)
            EndActions(listOf("Pixel 8", "Galaxy"), onAgain = {}, onOtherGame = {})
        }
    }
}
