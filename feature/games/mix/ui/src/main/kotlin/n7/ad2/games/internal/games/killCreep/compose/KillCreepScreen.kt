package n7.ad2.games.internal.games.killCreep.compose

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.killCreep.CreepSim
import n7.ad2.games.internal.games.killCreep.FloatText
import n7.ad2.games.internal.games.killCreep.KillCreepMatch
import n7.ad2.games.internal.games.killCreep.KillCreepViewModel
import n7.ad2.games.internal.games.killCreep.VisibleCreep
import n7.ad2.games.room.GameMode
import kotlin.math.roundToInt

private val MyColor = Color(0xFFF44336)
private val PeerColor = Color(0xFF448AFF)
private val Gold = Color(0xFFFFD166)
private val CreepSize = 46.dp

@Composable
internal fun KillCreepScreen(state: KillCreepViewModel.State, sim: CreepSim, elapsedMs: () -> Long, countdown: () -> Int, onTap: (x: Float, y: Float, aspect: Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(14.dp),
    ) {
        if (state.isEndGame) {
            KillCreepEnd(state)
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
        Header(state.match, state.peerName, remaining = (CreepSim.DURATION_MS - now).coerceIn(0, CreepSim.DURATION_MS))
        Box(Modifier.weight(1f).padding(top = 10.dp), contentAlignment = Alignment.Center) {
            Field(sim.visibleAt(now).filterNot { state.match.isTaken(it.id) }, state.floats, onTap, Modifier.fillMaxSize())
            // общий отсчет: крипы выходят у обоих игроков в один момент
            if (secondsToStart > 0) Text("$secondsToStart", color = Gold, fontSize = 96.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun Header(match: KillCreepMatch, peerName: String?, remaining: Long) {
    val timer = "⏱ 0:%02d".format(remaining / 1000)
    when (match.mode) {
        GameMode.SOLO -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("🪙 ${match.myGold}", color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(timer, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
            Text("🗡️ ${match.kills} · ❌ ${match.misses}/${KillCreepMatch.SOLO_MISSES}", color = AppTheme.color.textColor, style = AppTheme.style.body)
        }
        GameMode.VERSUS -> Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🪙 ${match.myGold}", color = MyColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(timer, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
                Text("${match.peerGold} 🪙", color = PeerColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.game_you), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
                Text(peerName.orEmpty(), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
            }
        }
        GameMode.COOP -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("🪙 ${match.score}", color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(timer, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
            Text("❌ ${match.misses}/${KillCreepMatch.COOP_MISSES}", color = AppTheme.color.textColor, style = AppTheme.style.body)
        }
    }
}

@Composable
private fun Field(creeps: List<VisibleCreep>, floats: List<FloatText>, onTap: (Float, Float, Float) -> Unit, modifier: Modifier) {
    val glow by rememberInfiniteTransition().animateFloat(0.3f, 1f, infiniteRepeatable(tween(350), RepeatMode.Reverse))
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.radialGradient(listOf(Color(0xFF3D4F37), Color(0xFF2A3426))))
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
        drawRect(Color(0xFFE53935), size = Size(size.width * creep.hp / creep.type.hp, size.height))
        // жёлтая черта - урон игрока: ниже нее крипа можно добить
        val threshold = size.width * creep.type.lastHitDamage / creep.type.hp
        drawRect(Gold, topLeft = Offset(threshold, 0f), size = Size(2.dp.toPx(), size.height))
    }
}

@Composable
private fun KillCreepEnd(state: KillCreepViewModel.State) {
    val match = state.match
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)) {
        when (match.mode) {
            GameMode.VERSUS -> {
                val title = when {
                    match.myGold > match.peerGold -> R.string.end_win
                    match.myGold < match.peerGold -> R.string.end_lose
                    else -> R.string.end_draw
                }
                Text(stringResource(title), color = AppTheme.color.textColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("🪙 ${match.myGold} : ${match.peerGold}", color = AppTheme.color.textColor, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                state.peerName?.let { Text(it, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body) }
            }
            GameMode.COOP -> Text(stringResource(R.string.end_shared_score, match.score), color = AppTheme.color.textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            GameMode.SOLO -> Text("🪙 ${match.myGold}", color = Gold, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        Text(stringResource(R.string.creep_kills, match.kills), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
        Text(stringResource(R.string.creep_lost, match.lost), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
        if (match.peerLeft) Text(stringResource(R.string.end_peer_left), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
        if (state.newRecord) Text(stringResource(R.string.end_new_record), color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}
