@file:OptIn(ExperimentalAnimationApi::class)

package n7.ad2.games.internal.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

    val state = viewModel.state.observeAsState().value ?: return
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn() + slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { fullHeight -> fullHeight },
            ) with fadeOut(animationSpec = tween(200))
        }
    ) { targetState ->
        when (targetState) {
            is GamesViewModel.State.Data -> GamesList(targetState.games, onGameClicked)
            GamesViewModel.State.Loading -> Loading()
        }
    }

    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}

@Composable
private fun Loading() {
    Box {
        Text(
            text = "Loading...",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun GamesList(
    games: List<GameVO>?,
    onGameClicked: (players: Players) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .systemBarsPadding(),
    ) {
        games?.forEach { gameData ->
//            Game(gameButtonData = gameData, onGameClicked = onGameClicked)
        }
    }
}