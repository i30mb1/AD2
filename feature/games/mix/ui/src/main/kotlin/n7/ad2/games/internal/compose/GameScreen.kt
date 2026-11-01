@file:OptIn(ExperimentalAnimationApi::class)

package n7.ad2.games.internal.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import n7.ad2.games.internal.progress.Progress

@Composable
internal fun GamesScreen(
    viewModel: GamesViewModel,
    drawerPercentListener: DrawerPercentListener?,
    onGameClicked: (game: GameVO) -> Unit,
    onDailyClicked: () -> Unit,
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
                        // в комнате с другими игру выбирает хост, она откроется у всех сама
                        session != null -> PartyRoom(
                            session = session,
                            games = data.games,
                            myAddress = data.party.myAddress,
                            actions = RoomActions(onPick = viewModel::pick, onReady = viewModel::setReady, onStart = viewModel::start, onLeave = viewModel::leave),
                        )
                        else -> GamesList(
                            games = data.games,
                            bests = data.bests,
                            progress = data.progress,
                            onGameClicked = onGameClicked,
                            onLeaderboardClicked = onLeaderboardClicked,
                            header = {
                                NearbyCard(data.party, onPlay = viewModel::play, onHelp = { showHelp = true })
                                DailyCard(data.daily, onClick = onDailyClicked)
                            },
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
    progress: Progress,
    onGameClicked: (game: GameVO) -> Unit,
    onLeaderboardClicked: () -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.games_title), color = AppTheme.color.textColor, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            LevelChip(progress)
        }
        header()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.games_title).uppercase(), color = AppTheme.color.textSecondaryColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
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
        // the games with a picture first, then two small cards in a row
        val (big, small) = games.partition { it is GameVO.Show || it is GameVO.Mid }
        big.forEachIndexed { index, game -> BigGameCard(game, bests[game.id], tilt = if (index % 2 == 0) 0f else -0.4f) { onGameClicked(game) } }
        small.chunked(2).forEach { row ->
            Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                row.forEachIndexed { index, game -> SmallGameCard(game, bests[game.id], tilt = if (index % 2 == 0) 0f else 0.6f, modifier = Modifier.weight(1f).fillMaxHeight()) { onGameClicked(game) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
