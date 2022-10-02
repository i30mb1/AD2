package n7.ad2.games.internal.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.android.DrawerPercentListener
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.data.Players

@Composable
internal fun GamesScreen(
    viewModel: GamesViewModel,
    drawerPercentListener: DrawerPercentListener,
    onGameClicked: (players: Players) -> Unit,
) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var drawerPercent by remember { mutableStateOf(0f) }

    val games = viewModel.games.observeAsState()
    GamesList(games.value, onGameClicked)

    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}

@Composable
private fun GamesList(
    games: List<GameVO>?,
    onGameClicked: (players: Players) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        games?.forEach { gameData ->
            Game(gameButtonData = gameData, onGameClicked = onGameClicked)
        }
    }
}