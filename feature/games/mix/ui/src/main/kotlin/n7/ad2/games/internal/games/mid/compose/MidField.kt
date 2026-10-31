package n7.ad2.games.internal.games.mid.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.roundToInt
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.games.mid.Attack
import n7.ad2.games.internal.games.mid.Creep
import n7.ad2.games.internal.games.mid.LanePoint
import n7.ad2.games.internal.games.mid.LaneSim
import n7.ad2.games.internal.games.mid.MidFloat
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.paper.PaperCreep
import n7.ad2.games.internal.paper.PaperLane
import n7.ad2.games.internal.paper.PaperLaneBackground
import n7.ad2.games.room.GameMode

private val CreepSize = 42.dp
private val HeroSize = 46.dp
private val FloatRise = 26.dp

/** Lane points on the paper background: the midline runs from the Radiant corner to the Dire one across the river */
internal object LaneDrawing {
    private val start = Offset(10f, 620f)
    private val end = Offset(360f, 10f)
    private const val HALF_WIDTH = 60f
    private val direction = end - start
    private val across = Offset(-direction.y, direction.x) / direction.getDistance()

    /** [flipped]: the Dire player sees the field turned around, so their creeps also come from the bottom */
    fun paper(point: LanePoint, flipped: Boolean): Offset {
        val p = start + direction * point.t + across * (point.aside * HALF_WIDTH)
        return if (flipped) Offset(PaperLane.WIDTH - p.x, PaperLane.HEIGHT - p.y) else p
    }
}

/** Paper coordinates to field pixels, the way the background is drawn: scaled to cover the field and centered */
internal class FieldMapping(width: Float, height: Float, private val flipped: Boolean) {
    private val unit = max(width / PaperLane.WIDTH, height / PaperLane.HEIGHT)
    private val dx = (width - PaperLane.WIDTH * unit) / 2
    private val dy = (height - PaperLane.HEIGHT * unit) / 2

    fun screen(point: LanePoint): Offset {
        val p = LaneDrawing.paper(point, flipped)
        return Offset(dx + p.x * unit, dy + p.y * unit)
    }
}

@Composable
internal fun MidField(state: MidViewModel.State, sim: LaneSim, now: Long, elapsedMs: () -> Long, actions: MidActions, modifier: Modifier) {
    val flipped = state.mySide == Side.DIRE
    val latest by rememberUpdatedState(state)
    val density = LocalDensity.current
    val creepRadius = with(density) { CreepSize.toPx() * 0.75f }
    val heroRadius = with(density) { HeroSize.toPx() * 0.7f }
    BoxWithConstraints(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .pointerInput(flipped) {
                detectTapGestures { tap ->
                    val mapping = FieldMapping(size.width.toFloat(), size.height.toFloat(), flipped)
                    val time = elapsedMs()
                    val current = latest
                    val creep = sim.creeps
                        .filter { sim.isAlive(it, time) && !current.match.isTaken(it.id) }
                        .map { it to (mapping.screen(sim.positionAt(it, time)) - tap).getDistance() }
                        .filter { (creep, distance) -> distance <= creepRadius * creep.type.size }
                        .minByOrNull { it.second }
                        ?.first
                    when {
                        creep != null -> actions.onCreepTap(creep.id)
                        (mapping.screen(myHeroPoint(current)) - tap).getDistance() <= heroRadius -> actions.onHeroTap()
                    }
                }
            },
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val mapping = remember(width, height, flipped) { FieldMapping(width, height, flipped) }
        PaperLaneBackground(Modifier.matchParentSize().graphicsLayer { rotationZ = if (flipped) 180f else 0f })

        sim.creeps.forEach { creep ->
            if (sim.isAlive(creep, now) && !state.match.isTaken(creep.id)) {
                key(creep.id) { CreepOnLane(creep, sim, now, state, mapping) }
            }
        }

        val myPoint = mapping.screen(myHeroPoint(state))
        val opponentHero = state.opponentHero
        HeroOnLane(state.myHero, myPoint, MeColor)
        state.myAttack?.let { SwingView(it, state.myHero, myPoint, sim, now, mapping, GoldColor) }
        when (state.mode) {
            GameMode.VERSUS -> {
                val point = mapping.screen(LaneSim.heroPoint(state.opponentSide))
                HeroOnLane(opponentHero, point, PeerColor)
                state.peerSwings.forEach { SwingView(it, opponentHero, point, sim, now, mapping, PeerColor) }
            }
            GameMode.COOP -> {
                val point = mapping.screen(LaneSim.heroPoint(state.mySide).copy(aside = COOP_ASIDE))
                HeroOnLane(state.peerHero, point, PeerColor)
                state.peerSwings.forEach { SwingView(it, state.peerHero, point, sim, now, mapping, PeerColor) }
            }
            GameMode.SOLO -> Unit
        }
        if (state.botHero != null) {
            val point = mapping.screen(LaneSim.heroPoint(state.opponentSide))
            HeroOnLane(state.botHero, point, PeerColor)
            state.botPlan.forEach { plan ->
                if (plan.startAt <= now && now < plan.landAt) {
                    val release = plan.startAt + state.botHero.attackPointMs
                    SwingView(Attack(plan.creepId, plan.startAt, release, plan.landAt), state.botHero, point, sim, now, mapping, PeerColor)
                }
            }
        }
        state.floats.forEach { float -> key(float.id) { FloatView(float, mapping) } }
    }
}

/** Together both heroes stand on the same side: mine on the left of the lane, the other one on the right */
private fun myHeroPoint(state: MidViewModel.State): LanePoint {
    val point = LaneSim.heroPoint(state.mySide)
    return if (state.mode == GameMode.COOP) point.copy(aside = -COOP_ASIDE) else point
}

private const val COOP_ASIDE = 0.75f

@Composable
private fun CreepOnLane(creep: Creep, sim: LaneSim, now: Long, state: MidViewModel.State, mapping: FieldMapping) {
    val density = LocalDensity.current
    val size = CreepSize * creep.type.size
    val sizePx = with(density) { size.toPx() }
    val center = mapping.screen(sim.positionAt(creep, now))
    val hp = sim.hpAt(creep, now)
    val reach = ((state.myHero?.damage ?: 0) * creep.type.heroDamage).toInt()
    // creeps of the far side face the near one
    val facesDown = creep.side != state.mySide
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.offset { IntOffset((center.x - sizePx / 2).roundToInt(), (center.y - sizePx * 0.8f).roundToInt()) },
    ) {
        HpBar(hp, creep.type.hp, reach, creep.side.color(), Modifier.size(width = size, height = 6.dp))
        PaperCreep(
            type = creep.type,
            side = creep.side,
            // the hint that a creep is in reach is only for training: in a game the health bar is all there is
            killable = state.training && hp <= reach,
            modifier = Modifier.size(size).graphicsLayer { scaleX = if (facesDown) -1f else 1f },
        )
    }
}

/** Health with a gold mark at my hit: when the health is left of the mark, a hit landing now takes the creep */
@Composable
private fun HpBar(hp: Int, maxHp: Int, reach: Int, color: Color, modifier: Modifier) {
    Canvas(modifier.clip(RoundedCornerShape(2.dp))) {
        drawRect(Color(0xFF14110F))
        drawRect(color, size = Size(size.width * hp / maxHp, size.height))
        val mark = size.width * reach / maxHp
        drawRect(GoldColor, topLeft = Offset(mark, 0f), size = Size(2.dp.toPx(), size.height))
    }
}

@Composable
private fun HeroOnLane(hero: MidHero?, center: Offset, ring: Color) {
    val density = LocalDensity.current
    val half = with(density) { HeroSize.toPx() / 2 }
    Box(Modifier.offset { IntOffset((center.x - half).roundToInt(), (center.y - half).roundToInt()) }) {
        HeroAvatar(hero, ring, size = HeroSize.value.toInt())
    }
}

/** A swing: a ring fills around the hero, then a ranged hero's projectile flies to the creep */
@Composable
private fun SwingView(attack: Attack, hero: MidHero?, heroCenter: Offset, sim: LaneSim, now: Long, mapping: FieldMapping, color: Color) {
    val density = LocalDensity.current
    val ringSize = HeroSize + 12.dp
    val half = with(density) { ringSize.toPx() / 2 }
    if (now < attack.releaseAt) {
        val progress = ((now - attack.startAt).toFloat() / (attack.releaseAt - attack.startAt)).coerceIn(0f, 1f)
        Canvas(Modifier.offset { IntOffset((heroCenter.x - half).roundToInt(), (heroCenter.y - half).roundToInt()) }.size(ringSize)) {
            val stroke = 4.dp.toPx()
            drawArc(Color.Black.copy(alpha = 0.35f), 0f, 360f, useCenter = false, topLeft = Offset(stroke / 2, stroke / 2), size = Size(size.width - stroke, size.height - stroke), style = Stroke(stroke))
            drawArc(color, -90f, 360f * progress, useCenter = false, topLeft = Offset(stroke / 2, stroke / 2), size = Size(size.width - stroke, size.height - stroke), style = Stroke(stroke))
        }
    } else if ((hero?.projectileSpeed ?: 0) > 0 && now < attack.landAt) {
        val creep = sim.creep(attack.creepId)
        val target = mapping.screen(sim.positionAt(creep, now))
        val flight = ((now - attack.releaseAt).toFloat() / (attack.landAt - attack.releaseAt).coerceAtLeast(1)).coerceIn(0f, 1f)
        val position = heroCenter + (target - heroCenter) * flight
        val dot = with(density) { 12.dp.toPx() }
        Canvas(Modifier.offset { IntOffset((position.x - dot / 2).roundToInt(), (position.y - dot / 2).roundToInt()) }.size(12.dp)) {
            drawCircle(Color(0xFF2B2622))
            drawCircle(color, radius = size.minDimension / 2 - 2.dp.toPx())
        }
    }
}

@Composable
private fun FloatView(float: MidFloat, mapping: FieldMapping) {
    val point = mapping.screen(float.point)
    val text = when (float.kind) {
        MidFloat.Kind.GOLD -> "+${float.gold}"
        MidFloat.Kind.DENY -> stringResource(R.string.mid_float_deny)
        MidFloat.Kind.MISS -> stringResource(R.string.mid_float_miss)
        MidFloat.Kind.OTHER_GOLD -> "${float.who.orEmpty()} +${float.gold}"
        MidFloat.Kind.OTHER_DENY -> stringResource(R.string.mid_float_other_deny, float.who.orEmpty())
    }
    val color = when (float.kind) {
        MidFloat.Kind.GOLD -> GoldColor
        MidFloat.Kind.DENY -> Color(0xFFFFF7E8)
        MidFloat.Kind.MISS -> Color(0xFFFF8A80)
        MidFloat.Kind.OTHER_GOLD, MidFloat.Kind.OTHER_DENY -> Color(0xFF9CC4FF)
    }
    // rises from above the creep's health bar and fades out by the end of its life
    val life = remember { Animatable(0f) }
    LaunchedEffect(Unit) { life.animateTo(1f, tween(MidFloat.LIFE_MS.toInt(), easing = LinearOutSlowInEasing)) }
    val lift = Modifier
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                val bottom = point.y - (CreepSize * 0.8f + FloatRise * life.value).toPx()
                placeable.place((point.x - placeable.width / 2f).roundToInt(), (bottom - placeable.height).roundToInt())
            }
        }
        .graphicsLayer {
            alpha = 1f - life.value * life.value
            if (float.kind == MidFloat.Kind.DENY) rotationZ = 8f
        }
    if (float.kind == MidFloat.Kind.DENY) {
        Text(
            text = text.uppercase(),
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            modifier = lift
                .background(DireColor, RoundedCornerShape(6.dp))
                .border(2.dp, Color(0xFFFFF7E8), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        )
    } else {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (float.kind == MidFloat.Kind.GOLD) 20.sp else 14.sp,
            style = TextStyle(shadow = Shadow(Color.Black, Offset(0f, 3f))),
            modifier = lift,
        )
    }
}
