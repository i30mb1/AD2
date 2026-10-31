@file:OptIn(ExperimentalAnimationApi::class)

package n7.ad2.games.internal.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import n7.ad2.core.ui.compose.view.LoadingScreen
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
    // игра, для которой открыт выбор режима
    var modeSheetFor by remember { mutableStateOf<GameVO?>(null) }

    val state = viewModel.state.collectAsState().value
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
                is GamesViewModel.State.Data -> GamesList(
                    games = targetState.games,
                    bests = (state as? GamesViewModel.State.Data)?.bests.orEmpty(),
                    onGameClicked = { game ->
                        if (game.quizId != null) modeSheetFor = game else onGameClicked(game, GameMode.SOLO)
                    },
                    onLeaderboardClicked = onLeaderboardClicked,
                )
                GamesViewModel.State.Loading -> LoadingScreen()
            }
        }
        modeSheetFor?.let { game ->
            ModeSheet(
                title = game.title,
                isKillCreep = game is GameVO.Apm,
                onDismiss = { modeSheetFor = null },
                onMode = { mode ->
                    modeSheetFor = null
                    onGameClicked(game, mode)
                },
            )
        }
    }

    DisposableEffect(key1 = Unit) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener?.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener?.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun GamesList(games: List<GameVO>, bests: Map<String, Int>, onGameClicked: (game: GameVO) -> Unit, onLeaderboardClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        games.forEach { gameData ->
            val badge = bests[gameData.quizId]?.let { stringResource(R.string.games_record_badge, it) }
            Game(gameData.title, gameData.backgroundImage, badge = badge, onGameClick = { onGameClicked(gameData) })
        }
        Text(
            text = stringResource(R.string.games_leaderboard),
            color = AppTheme.color.textColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.surface)
                .clickable(onClick = onLeaderboardClicked)
                .padding(14.dp),
        )
    }
}

@Composable
private fun ModeSheet(title: String, isKillCreep: Boolean, onDismiss: () -> Unit, onMode: (GameMode) -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(AppTheme.color.surface)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(36.dp, 4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF555555)))
            Text(title, color = AppTheme.color.textColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
            // у Kill Creep правила на время, а не вопросы с жизнями
            val soloHint = if (isKillCreep) R.string.creep_mode_solo_hint else R.string.games_mode_solo_hint
            val versusHint = if (isKillCreep) R.string.creep_mode_versus_hint else R.string.games_mode_versus_hint
            val coopHint = if (isKillCreep) R.string.creep_mode_coop_hint else R.string.games_mode_coop_hint
            ModeRow("👤", Color(0xFFF44336), stringResource(R.string.games_mode_solo), stringResource(soloHint)) { onMode(GameMode.SOLO) }
            ModeRow("⚔️", Color(0xFF448AFF), stringResource(R.string.games_mode_versus), stringResource(versusHint)) { onMode(GameMode.VERSUS) }
            ModeRow("🤝", Color(0xFF43A047), stringResource(R.string.games_mode_coop), stringResource(coopHint)) { onMode(GameMode.COOP) }
        }
    }
}

@Composable
private fun ModeRow(icon: String, color: Color, title: String, hint: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.background)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = AppTheme.color.textColor, fontWeight = FontWeight.SemiBold)
            Text(hint, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
        }
    }
}
