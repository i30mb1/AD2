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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import n7.ad2.streams.internal.chat.ChatMessage

private const val MAX_TEXT_LENGTH = 80
private const val MIN_READABLE_LUMINANCE = 0.35f
private val laneHeight = 30.dp
private val gap = 48.dp
private val textStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    shadow = Shadow(color = Color.Black, blurRadius = 6f),
)

/** Chat messages flying from right to left over the top of the video, which row and how fast is decided by [Ticker]. */
@Composable
internal fun FlyingChat(messages: Flow<ChatMessage>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val flying = remember { mutableStateListOf<TickerRun<TextLayoutResult>>() }
    var frameNanos by remember { mutableLongStateOf(0L) }
    val gapPx = with(LocalDensity.current) { gap.toPx() }
    val laneHeightPx = with(LocalDensity.current) { laneHeight.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthPx = constraints.maxWidth.toFloat()
        val ticker = remember(widthPx, gapPx) { Ticker<TextLayoutResult>(widthPx, gapPx) }

        LaunchedEffect(ticker, messages) {
            messages.collect { message ->
                val text = if (message.text.length > MAX_TEXT_LENGTH) message.text.take(MAX_TEXT_LENGTH - 1) + "…" else message.text
                val layout = textMeasurer.measure(text = text, style = textStyle.copy(color = readableColor(message.color)), maxLines = 1)
                ticker.offer(layout, System.nanoTime())
            }
        }
        LaunchedEffect(ticker) {
            flying.clear()
            while (true) {
                withFrameNanos { now ->
                    frameNanos = now
                    flying.removeAll { it.x(widthPx, now) + it.width < 0 }
                    flying += ticker.start(now) { layout -> layout.size.width.toFloat() }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(laneHeight * Ticker.MAX_LANES),
        ) {
            val now = frameNanos
            flying.forEach { run ->
                val y = run.lane * laneHeightPx + (laneHeightPx - run.message.size.height) / 2
                drawText(run.message, topLeft = Offset(run.x(widthPx, now), y))
            }
        }
    }
}

/** Twitch lets people pick any color for their name, a dark blue would be lost on the video. */
internal fun readableColor(argb: Int?): Color {
    val color = argb?.let(::Color) ?: return Color.White
    return if (color.luminance() < MIN_READABLE_LUMINANCE) lerp(color, Color.White, 0.5f) else color
}
