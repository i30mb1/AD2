package n7.ad2.games.internal.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import coil.request.ImageRequest
import n7.ad2.games.internal.games.show.points

private const val PRESSED_SCALE = 0.95f
private const val STAGE_IN_MS = 260
private const val STAGE_OUT_MS = 140

/** The finger presses the paper in and lets it spring back */
private val pressSpring = spring<Float>(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium)

/** Figures of the same width: a changing number does not push its neighbours */
internal val Digits = TextStyle(fontFeatureSettings = "tnum")

/** What comes later unfolds and pushes the rest down smoothly instead of at once */
internal val Unfold: EnterTransition = fadeIn(tween(220)) + expandVertically(spring(stiffness = Spring.StiffnessMediumLow), expandFrom = Alignment.Top)
internal val Fold: ExitTransition = fadeOut(tween(140)) + shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow), shrinkTowards = Alignment.Top)

/** A mark, a tag or a figure that pops in */
internal val PopIn: EnterTransition = fadeIn(tween(120)) + scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), initialScale = 0.6f)

/**
 * A tap the finger feels: the element sinks while pressed and springs back when let go.
 * Goes first in the chain, so the shadow, the paper and the text sink together while the touch area stays.
 */
internal fun Modifier.pressClickable(enabled: Boolean = true, role: Role? = Role.Button, onClick: () -> Unit): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    trackPress(enabled) { pressed = it }
        .clickable(interactionSource = null, indication = null, enabled = enabled, role = role, onClick = onClick)
        .pressScale(pressed && enabled)
}

/** [pressClickable] for one of a group to choose from */
internal fun Modifier.pressSelectable(selected: Boolean, enabled: Boolean = true, role: Role? = Role.RadioButton, onClick: () -> Unit): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    trackPress(enabled) { pressed = it }
        .selectable(selected = selected, interactionSource = null, indication = null, enabled = enabled, role = role, onClick = onClick)
        .pressScale(pressed && enabled)
}

/** The sink of a press the element handles itself, as a key that acts as soon as it is touched */
internal fun Modifier.pressScale(pressed: Boolean): Modifier = composed {
    val scale by animateFloatAsState(if (pressed) PRESSED_SCALE else 1f, pressSpring, label = "press")
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Down at once, even in a list that may scroll; up when the finger leaves or the list takes the gesture */
private fun Modifier.trackPress(enabled: Boolean, onPressed: (Boolean) -> Unit): Modifier = pointerInput(enabled) {
    if (!enabled) return@pointerInput
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        onPressed(true)
        waitForUpOrCancellation()
        onPressed(false)
    }
}

/** One stage of a screen gives way to the next: the old one fades out, the new one fades in rising a little */
internal fun <S> AnimatedContentTransitionScope<S>.stageChange(): ContentTransform =
    (fadeIn(tween(STAGE_IN_MS, delayMillis = STAGE_OUT_MS / 2)) + slideInVertically(tween(STAGE_IN_MS)) { it / 20 }) togetherWith
        fadeOut(tween(STAGE_OUT_MS)) using SizeTransform(clip = false)

/** A number that rolls to its new value: up when it grows, down when it falls */
@Composable
internal fun RollingNumber(value: Int, modifier: Modifier = Modifier, format: (Int) -> String = ::points, text: @Composable (String) -> Unit) {
    AnimatedContent(
        targetState = value,
        modifier = modifier,
        transitionSpec = {
            val up = targetState > initialState
            (slideInVertically { if (up) it else -it } + fadeIn()) togetherWith (slideOutVertically { if (up) -it else it } + fadeOut()) using SizeTransform(clip = false)
        },
        label = "number",
    ) { number -> text(format(number)) }
}

/**
 * A part that pops in and fades out. Unlike [AnimatedVisibility] called inside a box of a column or a row,
 * it does not reach for the scope of the column around.
 */
@Composable
internal fun Appear(visible: Boolean, modifier: Modifier = Modifier, enter: EnterTransition = PopIn, exit: ExitTransition = fadeOut(tween(140)), content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(visible = visible, modifier = modifier, enter = enter, exit = exit, label = "appear", content = content)
}

/** The last value that was not null: a part that leaves keeps showing what it had */
@Composable
internal fun <T : Any> rememberLast(value: T?): T? {
    val last = remember { arrayOfNulls<Any>(1) }
    if (value != null) last[0] = value
    @Suppress("UNCHECKED_CAST")
    return last[0] as T?
}

/** A picture fades in once it is loaded instead of popping up; one already in memory comes at once */
@Composable
internal fun fadeInImage(data: Any?): ImageRequest {
    val context = LocalContext.current
    return remember(data) { ImageRequest.Builder(context).data(data).crossfade(true).build() }
}
