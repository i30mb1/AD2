package n7.ad2.games.internal.games.invoker.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.Buzz
import n7.ad2.games.internal.compose.BuzzOnIncrease
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.games.invoker.INVOKE_IMAGE
import n7.ad2.games.internal.games.invoker.InvokerRun
import n7.ad2.games.internal.games.invoker.InvokerSpell
import n7.ad2.games.internal.games.invoker.InvokerViewModel
import n7.ad2.games.internal.games.invoker.Orb
import n7.ad2.games.internal.games.invoker.OrbsDiff
import n7.ad2.games.internal.games.invoker.spellQueue
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.room.GameMode

@Immutable
internal class InvokerActions(
    val onOrb: (Orb) -> Unit = {},
    val onInvoke: () -> Unit = {},
    val onHint: () -> Unit = {},
    val onAgain: () -> Unit = {},
    val onOtherGame: () -> Unit = {},
)

private val Light = Color(0xFFF4EEE3)
private val LightDim = Color(0xFFC9C2B6)
private val WrongRing = Color(0xFFA33F36)
private val WrongText = Color(0xFFE58C80)

@Composable
internal fun InvokerScreen(state: InvokerViewModel.State, elapsedMs: () -> Long, countdown: () -> Int, actions: InvokerActions) {
    BuzzOnIncrease(state.run.invoked, Buzz.GOOD)
    BuzzOnIncrease(state.run.mistakes, Buzz.BAD)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (state.phase == InvokerViewModel.State.Phase.END) {
            InvokerEnd(state, actions)
        } else {
            InvokerPlay(state, elapsedMs, countdown, actions)
        }
    }
}

@Composable
private fun ColumnScope.InvokerPlay(state: InvokerViewModel.State, elapsedMs: () -> Long, countdown: () -> Int, actions: InvokerActions) {
    // a frame clock for the time left and the countdown
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
    // a keyboard plays too: Q, W, E and R as in the game
    val focus = remember { FocusRequester() }
    val currentActions by rememberUpdatedState(actions)
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    val run = state.run
    Column(
        Modifier
            .weight(1f)
            .focusRequester(focus)
            .focusable()
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (event.key) {
                    Key.Q -> currentActions.onOrb(Orb.QUAS)
                    Key.W -> currentActions.onOrb(Orb.WEX)
                    Key.E -> currentActions.onOrb(Orb.EXORT)
                    Key.R -> currentActions.onInvoke()
                    else -> return@onKeyEvent false
                }
                true
            },
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Header(state, timeLeftMs = (InvokerViewModel.RUN_MS - now.coerceAtLeast(0)).coerceAtLeast(0))
        Box {
            TargetCard(run, actions)
            if (secondsToStart > 0) {
                Text(
                    "$secondsToStart",
                    color = Ink,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.matchParentSize().clip(RoundedCornerShape(18.dp)).background(PaperCard.copy(alpha = 0.92f)).padding(top = 40.dp),
                )
            }
        }
        NextRow(run)
        OrbsNow(run)
        Spacer(Modifier.weight(1f))
        Buttons(actions)
    }
}

/** The title, the score, the streak with the time left; over the network the team score or the best of the others */
@Composable
private fun Header(state: InvokerViewModel.State, timeLeftMs: Long) {
    val seconds = ((timeLeftMs + 999) / 1000).toInt()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.invoker), color = Light, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            val leader = state.leader
            if (leader != null) {
                val label = when {
                    state.mode == GameMode.COOP -> stringResource(R.string.invoker_team, points(state.teamScore))
                    state.rivals.size == 1 -> "${leader.name} · ${points(leader.score)}"
                    else -> stringResource(R.string.invoker_leader, leader.name, points(leader.score))
                }
                Text(label, color = LightDim, fontSize = 12.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(points(state.run.score), color = Light, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.invoker_streak, state.run.streak, "%d:%02d".format(seconds / 60, seconds % 60)), color = LightDim, fontSize = 11.sp)
        }
    }
}

/** The spell to invoke; the recipe opens on a tap for half the points */
@Composable
private fun TargetCard(run: InvokerRun, actions: InvokerActions) {
    val spell = run.target
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(0.6f)
            .shadow(6.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, PaperEdge, shape)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.invoker_assemble), color = InkDim, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        AsyncImage(
            model = spell.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(104.dp).clip(RoundedCornerShape(14.dp)).border(3.dp, PaperEdge, RoundedCornerShape(14.dp)),
        )
        Text(spell.title, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        if (run.hinted) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.alpha(0.7f)) {
                spell.orbs.forEach { orb -> OrbImage(orb, 22.dp) }
            }
        } else {
            Text(
                stringResource(R.string.invoker_hint),
                color = InkDim,
                fontSize = 12.sp,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(role = Role.Button, onClick = actions.onHint).padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

/** The next spells get smaller and fainter; the points of the last spell on the right */
@Composable
private fun NextRow(run: InvokerRun) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.invoker_next), color = LightDim, fontSize = 12.sp)
        run.next.forEachIndexed { index, spell ->
            AsyncImage(
                model = spell.imageUrl,
                contentDescription = spell.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(36.dp - 4.dp * index).alpha(1f - 0.18f * index).clip(RoundedCornerShape(8.dp)),
            )
        }
        Spacer(Modifier.weight(1f))
        run.lastPoints?.let { Text("+$it", color = Color(0xFFF3C969), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) }
    }
}

/** The orbs now; after a wrong invoke the ones that do not belong are ringed red and the missing ones are named */
@Composable
private fun OrbsNow(run: InvokerRun) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.invoker_orbs), color = LightDim, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(3) { index ->
                val orb = run.orbs.getOrNull(index)
                val ring = if (run.wrong?.extra?.contains(index) == true) WrongRing else PaperEdge
                Box(Modifier.size(58.dp).shadow(4.dp, CircleShape).clip(CircleShape).background(Color(0xFF2F3136)).border(3.dp, if (orb == null) DimColor.copy(alpha = 0.4f) else ring, CircleShape)) {
                    if (orb != null) OrbImage(orb, 58.dp)
                }
            }
        }
        val wrong = run.wrong
        Text(wrong?.let { missingText(it) }.orEmpty(), color = WrongText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun missingText(wrong: OrbsDiff): String = when (wrong.missing.size) {
    0 -> ""
    1 -> stringResource(R.string.invoker_need_one, wrong.missing.single().title)
    else -> stringResource(R.string.invoker_need, wrong.missing.joinToString { it.title })
}

@Composable
private fun OrbImage(orb: Orb, size: Dp) {
    AsyncImage(model = orb.imageUrl, contentDescription = orb.title, contentScale = ContentScale.Crop, modifier = Modifier.size(size).clip(CircleShape))
}

/** Q W E add orbs, R invokes; a touch counts at once, without waiting for the finger to go up */
@Composable
private fun Buttons(actions: InvokerActions) {
    val current by rememberUpdatedState(actions)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Orb.entries.forEach { orb ->
            KeyButton(orb.imageUrl, orb.key, orb.title, PaperCard, CircleShape, Modifier.weight(1f)) { current.onOrb(orb) }
        }
        KeyButton(INVOKE_IMAGE, "R", "Invoke", ShowGold, RoundedCornerShape(10.dp), Modifier.weight(1f)) { current.onInvoke() }
    }
}

@Composable
private fun KeyButton(image: String, key: String, label: String, color: Color, imageShape: androidx.compose.ui.graphics.Shape, modifier: Modifier, onPress: () -> Unit) {
    val press by rememberUpdatedState(onPress)
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .height(92.dp)
            .shadow(5.dp, shape)
            .clip(shape)
            .background(color)
            .border(3.dp, PaperEdge, shape)
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    press()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        AsyncImage(model = image, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(46.dp).clip(imageShape))
        Text(key, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InvokerPlayPreview() {
    val run = InvokerRun(listOf(InvokerSpell.SUN_STRIKE) + spellQueue(3), score = 2_150, streak = 6, lastPoints = 150)
        .press(Orb.EXORT).press(Orb.EXORT).press(Orb.WEX).invoke().hint()
    AppTheme(isDarkTheme = true) {
        InvokerScreen(InvokerViewModel.State.start(GameMode.SOLO, run, rivals = emptyList()), elapsedMs = { 22_000 }, countdown = { 0 }, actions = InvokerActions())
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InvokerVersusPreview() {
    val run = InvokerRun(spellQueue(4), score = 1_240, streak = 3, lastPoints = 120).press(Orb.QUAS).press(Orb.WEX)
    AppTheme(isDarkTheme = true) {
        val rivals = listOf(InvokerViewModel.Rival("p", "Pixel 8", score = 1_310), InvokerViewModel.Rival("g", "Galaxy", score = 880))
        InvokerScreen(InvokerViewModel.State.start(GameMode.VERSUS, run, rivals), elapsedMs = { -1_200 }, countdown = { 2 }, actions = InvokerActions())
    }
}
