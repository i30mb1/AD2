package n7.ad2.games.internal.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.Tabs
import n7.ad2.games.room.GameMode

/** Игр больше, чем помещается в строку - вкладки прокручиваются */
@Composable
private fun GameChips(items: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items.forEachIndexed { index, title ->
            Text(
                text = title,
                color = if (index == selected) Color.White else AppTheme.color.textSecondaryColor,
                style = AppTheme.style.body,
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (index == selected) MeColor else AppTheme.color.surface)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
internal fun LeaderboardScreen(state: LeaderboardViewModel.State, onGame: (String) -> Unit, onMode: (GameMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        Text(stringResource(R.string.leaderboard_title), color = AppTheme.color.textColor, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(14.dp))
        GameChips(
            items = listOf(stringResource(R.string.spell_cost), stringResource(R.string.guess_hero), stringResource(R.string.higher_lower), stringResource(R.string.build_item), stringResource(R.string.mid)),
            selected = LeaderboardViewModel.State.GAMES.indexOf(state.gameId),
            onSelect = { onGame(LeaderboardViewModel.State.GAMES[it]) },
        )
        Spacer(Modifier.height(8.dp))
        Tabs(
            items = listOf(stringResource(R.string.games_mode_solo), stringResource(R.string.games_mode_versus), stringResource(R.string.games_mode_coop)),
            selected = state.mode.ordinal,
            onSelect = { onMode(GameMode.entries[it]) },
        )
        Spacer(Modifier.height(12.dp))
        if (state.records.isEmpty()) {
            Text(stringResource(R.string.leaderboard_empty), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            itemsIndexed(state.records, key = { _, record -> record.playerId }) { index, record ->
                val isMe = record.playerId == state.myId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.surface)
                        .then(if (isMe) Modifier.border(1.dp, MeColor, RoundedCornerShape(10.dp)) else Modifier)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${index + 1}", color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body, modifier = Modifier.width(24.dp))
                    if (record.playerId in state.online) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF43A047)))
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isMe) stringResource(R.string.lobby_you, record.playerName) else record.playerName,
                        color = AppTheme.color.textColor,
                        style = AppTheme.style.body,
                        modifier = Modifier.weight(1f),
                    )
                    Text(record.score.toString(), color = AppTheme.color.textColor, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(stringResource(R.string.leaderboard_hint), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
    }
}
