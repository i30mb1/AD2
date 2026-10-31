@file:OptIn(ExperimentalAnimationApi::class)

package n7.ad2.games.internal.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.room.GameMode

@Composable
internal fun GamesScreen(
    viewModel: GamesViewModel,
    drawerPercentListener: DrawerPercentListener?,
    onGameClicked: (game: GameVO, mode: GameMode) -> Unit,
    onLeaderboardClicked: () -> Unit,
) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    // без drawer (demo-приложение) отступ под статус-бар нужен всегда
    var drawerPercent by remember { mutableStateOf(if (drawerPercentListener == null) 1f else 0f) }

    val state = viewModel.state.collectAsState().value
    // помощь «никого не нашли» открывается поверх списка, назад - закрывает её
    var showHelp by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showHelp) { showHelp = false }
    Box(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state,
            modifier = Modifier.padding(top = insetsTop * drawerPercent),
            transitionSpec = {
                fadeIn() + slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { fullHeight -> fullHeight },
                ) with fadeOut(animationSpec = tween(200))
            },
            contentKey = { it::class },
        ) { targetState ->
            when (targetState) {
                is GamesViewModel.State.Data -> {
                    val data = state as? GamesViewModel.State.Data ?: targetState
                    val session = data.party.session
                    when {
                        showHelp -> PartyHelp(data.party, onJoinManual = { address ->
                            showHelp = false
                            viewModel.joinManual(address)
                        })
                        // вдвоём список игр - это общий выбор, игра откроется сама, когда выбор совпадёт
                        session != null -> PartySelect(data.party, session.peer.name, data.games, onPick = viewModel::pick, onLeave = viewModel::leave)
                        else -> GamesList(
                            games = data.games,
                            bests = data.bests,
                            onGameClicked = { game -> onGameClicked(game, GameMode.SOLO) },
                            onLeaderboardClicked = onLeaderboardClicked,
                            header = { NearbyCard(data.party, onPlay = viewModel::play, onHelp = { showHelp = true }) },
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(key1 = Unit) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener?.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener?.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun GamesList(
    games: List<GameVO>,
    bests: Map<String, Int>,
    onGameClicked: (game: GameVO) -> Unit,
    onLeaderboardClicked: () -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.games_title), color = AppTheme.color.textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(
                text = "🏆 " + stringResource(R.string.games_records),
                color = AppTheme.color.textSecondaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, LineColor, RoundedCornerShape(16.dp))
                    .clickable(onClick = onLeaderboardClicked)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }
        header()
        games.forEach { game -> GameRow(game, bests[game.quizId]) { onGameClicked(game) } }
    }
}

@Composable
private fun GameRow(game: GameVO, record: Int?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.surface)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GameCover(game, 56.dp, shape = RoundedCornerShape(10.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(game.title, color = AppTheme.color.textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(game.rules()), color = DimColor, fontSize = 12.sp)
            if (record != null) Text(stringResource(R.string.games_record_badge, record), color = GoldColor, fontSize = 11.sp)
        }
        Text("›", color = DimColor, fontSize = 22.sp)
    }
}

private fun GameVO.rules(): Int = when (this) {
    is GameVO.Show -> R.string.games_rules_show
    is GameVO.Mid -> R.string.games_rules_mid
    is GameVO.XO -> R.string.games_rules_xo
}
