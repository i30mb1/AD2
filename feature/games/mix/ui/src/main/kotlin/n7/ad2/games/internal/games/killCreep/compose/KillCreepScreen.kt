package n7.ad2.games.internal.games.killCreep.compose

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.Avatar
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.DuoHud
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.compose.SoloHud
import n7.ad2.games.internal.games.killCreep.CreepSim
import n7.ad2.games.internal.games.killCreep.FloatText
import n7.ad2.games.internal.games.killCreep.KillCreepMatch
import n7.ad2.games.internal.games.killCreep.KillCreepViewModel
import n7.ad2.games.internal.games.killCreep.VisibleCreep
import n7.ad2.games.internal.games.skillmp.compose.EndActions
import n7.ad2.games.internal.games.skillmp.compose.RecordPlate
import n7.ad2.games.room.GameMode

private val Gold = GoldColor
private val CreepSize = 46.dp

@Composable
internal fun KillCreepScreen(
    state: KillCreepViewModel.State,
    sim: CreepSim,
    elapsedMs: () -> Long,
    countdown: () -> Int,
    onTap: (x: Float, y: Float, aspect: Float) -> Unit,
    onAgain: () -> Unit = {},
    onOtherGame: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (state.isEndGame) {
            KillCreepEnd(state, onAgain, onOtherGame)
            return@Column
        }
        // кадровый таймер: позиции и HP крипов - чистая функция времени
        var now by remember { mutableLongStateOf(elapsedMs()) }
        var secondsToStart by remember { mutableIntStateOf(countdown()) }
        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis {
                    now = elapsedMs()
                    secondsToStart = countdown()
                }
            }
        }
        val remaining = (CreepSim.DURATION_MS - now).coerceIn(0, CreepSim.DURATION_MS)
        Header(state.match, state.peerName, remaining)
        Box(Modifier.weight(1f).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Field(sim.visibleAt(now).filterNot { state.match.isTaken(it.id) }, state.floats, onTap, Modifier.fillMaxSize())
            // общий отсчет: крипы выходят у обоих игроков в один момент
            if (secondsToStart > 0) Countdown(secondsToStart)
        }
        HintBar(state.match)
    }
}

@Composable
private fun Header(match: KillCreepMatch, peerName: String?, remaining: Long) {
    val timer = "0:%02d".format(remaining / 1000)
    when (match.mode) {
        GameMode.SOLO -> SoloHud(score = stringResource(R.string.creep_gold, match.myGold), extra = timer, lives = null, maxLives = 0)
        GameMode.VERSUS -> DuoHud(
            me = stringResource(R.string.game_me_letter),
            myScore = match.myGold.toString(),
            peerName = peerName.orEmpty(),
            peerScore = match.peerGold.toString(),
            centerLabel = stringResource(R.string.game_time_label),
            centerValue = timer,
            peerAnswered = false,
        )
        GameMode.COOP -> SoloHud(score = stringResource(R.string.game_shared_points, match.score), extra = timer, lives = null, maxLives = 0)
    }
}

/** Подсказка, как добивать, и промахи там, где они ограничены */
@Composable
private fun HintBar(match: KillCreepMatch) {
    val limit = when (match.mode) {
        GameMode.SOLO -> KillCreepMatch.SOLO_MISSES
        GameMode.COOP -> KillCreepMatch.COOP_MISSES
        GameMode.VERSUS -> null
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.creep_hint), color = AppTheme.color.textSecondaryColor, fontSize = 12.sp, modifier = Modifier.weight(1f))
        if (limit != null) Text(stringResource(R.string.creep_misses, match.misses, limit), color = MeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Countdown(seconds: Int) {
    Box(
        Modifier.size(88.dp).clip(CircleShape).background(AppTheme.color.background.copy(alpha = 0.85f)).border(4.dp, MeColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("$seconds", color = AppTheme.color.textColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Field(creeps: List<VisibleCreep>, floats: List<FloatText>, onTap: (Float, Float, Float) -> Unit, modifier: Modifier) {
    val glow by rememberInfiniteTransition().animateFloat(0.3f, 1f, infiniteRepeatable(tween(350), RepeatMode.Reverse))
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.surface)
            .pointerInput(Unit) {
                detectTapGestures { offset -> onTap(offset.x / size.width, offset.y / size.height, size.height.toFloat() / size.width) }
            },
    ) {
        val density = LocalDensity.current
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        creeps.forEach { creep ->
            val sizePx = with(density) { (CreepSize * creep.type.size).toPx() }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset { IntOffset((creep.x * width - sizePx / 2).roundToInt(), (creep.y * height - sizePx / 2).roundToInt()) },
            ) {
                HpBar(creep, Modifier.size(width = CreepSize * creep.type.size, height = 6.dp))
                Text(
                    text = creep.type.emoji,
                    fontSize = (28 * creep.type.size).sp,
                    modifier = if (creep.isKillable) Modifier.shadow((12 * glow).dp, RoundedCornerShape(50), ambientColor = Gold, spotColor = Gold) else Modifier,
                )
            }
        }
        floats.forEach { float ->
            val color = when (float.kind) {
                FloatText.Kind.GOLD -> Gold
                FloatText.Kind.MISS -> Color(0xFFE57373)
                FloatText.Kind.PEER -> PeerColor
            }
            Text(
                text = if (float.kind == FloatText.Kind.MISS) stringResource(R.string.creep_early) else float.text,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier.offset { IntOffset((float.x * width).roundToInt() - 40, (float.y * height).roundToInt() - 90) },
            )
        }
    }
}

@Composable
private fun HpBar(creep: VisibleCreep, modifier: Modifier) {
    Canvas(modifier) {
        drawRect(Color(0xFF111111))
        drawRect(if (creep.isKillable) Gold else Color(0xFFE53935), size = Size(size.width * creep.hp / creep.type.hp, size.height))
        // жёлтая черта - урон игрока: ниже нее крипа можно добить
        val threshold = size.width * creep.type.lastHitDamage / creep.type.hp
        drawRect(Gold, topLeft = Offset(threshold, 0f), size = Size(2.dp.toPx(), size.height))
    }
}

@Composable
private fun KillCreepEnd(state: KillCreepViewModel.State, onAgain: () -> Unit, onOtherGame: () -> Unit) {
    val match = state.match
    Column(Modifier.fillMaxSize().padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (match.mode) {
            GameMode.VERSUS -> {
                val title = when {
                    match.myGold > match.peerGold -> R.string.end_win
                    match.myGold < match.peerGold -> R.string.end_lose
                    else -> R.string.end_draw
                }
                Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                EndCard {
                    EndPlayer(stringResource(R.string.game_me_letter), stringResource(R.string.game_you), MeColor)
                    Text("${match.myGold} : ${match.peerGold}", color = AppTheme.color.textColor, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    EndPlayer(state.peerName.orEmpty(), state.peerName.orEmpty(), PeerColor)
                }
            }
            GameMode.COOP -> EndCard { Text(stringResource(R.string.end_shared_score, match.score), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            GameMode.SOLO -> EndCard { Text(stringResource(R.string.creep_gold, match.myGold), color = Gold, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EndStat(match.kills.toString(), stringResource(R.string.creep_kills_label), Modifier.weight(1f))
            EndStat(match.lost.toString(), stringResource(R.string.creep_lost_label), Modifier.weight(1f))
        }
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = DimColor, fontSize = 13.sp)
        if (state.newRecord) RecordPlate(state.previousBest, now = if (match.mode == GameMode.COOP) match.score else match.myGold)
        Spacer(Modifier.weight(1f))
        EndActions(state.peerName, onAgain, onOtherGame)
    }
}

@Composable
private fun EndCard(content: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(AppTheme.color.surface).padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) { content() }
}

@Composable
private fun EndPlayer(avatarName: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Avatar(avatarName, color, 48.dp)
        Text(label, color = AppTheme.color.textColor, fontSize = 12.sp)
    }
}

@Composable
private fun EndStat(value: String, label: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = AppTheme.color.textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = DimColor, fontSize = 11.sp)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun KillCreepVersusPreview() {
    val state = KillCreepViewModel.State(
        match = KillCreepMatch.start(GameMode.VERSUS, isHost = true),
        peerName = "Pixel 8",
        floats = listOf(FloatText(1, 0.5f, 0.4f, "+42", FloatText.Kind.GOLD)),
        isEndGame = false,
        newRecord = false,
        previousBest = null,
    )
    AppTheme(isDarkTheme = true) { KillCreepScreen(state, CreepSim(seed = 7), elapsedMs = { 19_000 }, countdown = { 0 }, onTap = { _, _, _ -> }) }
}
