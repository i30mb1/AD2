package n7.ad2.streams.internal.stream.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import n7.ad2.streams.internal.chat.ChatMessage
import n7.ad2.streams.internal.model.ChatDensity

private const val NANOS_IN_SECOND = 1_000_000_000f
private const val MAX_TEXT_LENGTH = 80
private const val MAX_WAITING = 30

/** A message waits this long for a free row, an older one is no longer about what is on the screen. */
private const val MAX_WAIT_NANOS = 4_000_000_000L

/** A message crosses the whole screen in this time, the long ones a bit slower since all rows move at one speed. */
private const val SCREEN_CROSS_SECONDS = 8f
private const val MIN_READABLE_LUMINANCE = 0.35f
private val laneHeight = 30.dp
private val gap = 48.dp
private val textStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    shadow = Shadow(color = Color.Black, blurRadius = 6f),
)

private class Waiting(val message: ChatMessage, val receivedNanos: Long)

private class Flying(val layout: TextLayoutResult, val lane: Int, val startNanos: Long)

/**
 * Chat messages flying from right to left over the top of the video, like on Niconico.
 * Every row takes the next message only when the previous one has fully entered the screen, so the messages never overlap;
 * when the chat is faster than the rows, the oldest messages are dropped.
 */
@Composable
internal fun FlyingChat(messages: Flow<ChatMessage>, density: ChatDensity, modifier: Modifier = Modifier) {
    if (density == ChatDensity.OFF) return
    val textMeasurer = rememberTextMeasurer()
    val waiting = remember { ArrayDeque<Waiting>() }
    val flying = remember { mutableStateListOf<Flying>() }
    var frameNanos by remember { mutableLongStateOf(0L) }
    val gapPx = with(LocalDensity.current) { gap.toPx() }
    val laneHeightPx = with(LocalDensity.current) { laneHeight.toPx() }

    LaunchedEffect(messages) {
        messages.collect { message ->
            waiting.addLast(Waiting(message, System.nanoTime()))
            if (waiting.size > MAX_WAITING) waiting.removeFirst()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthPx = constraints.maxWidth.toFloat()
        val speed = widthPx / SCREEN_CROSS_SECONDS
        fun x(item: Flying, now: Long): Float = widthPx - speed * (now - item.startNanos) / NANOS_IN_SECOND

        LaunchedEffect(density, widthPx) {
            flying.removeAll { it.lane >= density.lanes }
            while (true) {
                withFrameNanos { now ->
                    frameNanos = now
                    flying.removeAll { x(it, now) + it.layout.size.width < 0 }
                    val nowSystem = System.nanoTime()
                    while (waiting.isNotEmpty() && nowSystem - waiting.first().receivedNanos > MAX_WAIT_NANOS) waiting.removeFirst()
                    for (lane in 0 until density.lanes) {
                        if (waiting.isEmpty()) break
                        val last = flying.lastOrNull { it.lane == lane }
                        val isFree = last == null || x(last, now) + last.layout.size.width + gapPx <= widthPx
                        if (isFree) {
                            val message = waiting.removeFirst().message
                            val layout = textMeasurer.measure(
                                text = message.text.take(MAX_TEXT_LENGTH),
                                style = textStyle.copy(color = readableColor(message.color)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            flying.add(Flying(layout, lane, now))
                        }
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(laneHeight * density.lanes),
        ) {
            val now = frameNanos
            flying.forEach { item ->
                val y = item.lane * laneHeightPx + (laneHeightPx - item.layout.size.height) / 2
                drawText(item.layout, topLeft = Offset(x(item, now), y))
            }
        }
    }
}

/** Twitch lets people pick any color for their name, a dark blue would be lost on the video. */
private fun readableColor(argb: Int?): Color {
    val color = argb?.let(::Color) ?: return Color.White
    return if (color.luminance() < MIN_READABLE_LUMINANCE) lerp(color, Color.White, 0.5f) else color
}
