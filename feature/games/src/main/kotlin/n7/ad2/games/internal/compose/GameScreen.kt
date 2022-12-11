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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.android.DrawerPercentListener
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.data.GameVO
import n7.ad2.ui.compose.view.LoadingScreen

@Composable
internal fun GamesScreen(
    viewModel: GamesViewModel,
    drawerPercentListener: DrawerPercentListener,
    onGameClicked: (game: GameVO) -> Unit,
) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var drawerPercent by remember { mutableStateOf(0f) }

    val state = viewModel.state.collectAsState().value
    AnimatedContent(
        targetState = state,
        modifier = Modifier.padding(top = insetsTop * drawerPercent),
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
    LoadingScreen()
}

@Composable
private fun GamesList(
    games: List<GameVO>?,
    onGameClicked: (game: GameVO) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        games?.forEach { gameData ->
            Game(gameData.title, gameData.backgroundImage, onGameClick = { onGameClicked(gameData) })
        }
    }
}