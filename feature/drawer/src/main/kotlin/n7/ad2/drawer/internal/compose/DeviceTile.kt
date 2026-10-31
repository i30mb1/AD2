package n7.ad2.drawer.internal.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.frameCounter.toComposeColor
import n7.ad2.drawer.internal.domain.vo.VOPerformance
import n7.ad2.feature.drawer.R

private val tileShape = RoundedCornerShape(10.dp)
private val smoothColor = Color(0xFF4CAF50)

/** CPU / RAM / FPS of the device with a short graph each, [isJank] puts a smooth/jank dot in the header once it is known. */
@Composable
internal fun DeviceTile(performance: List<VOPerformance>, isJank: Boolean?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(tileShape)
            .background(AppTheme.color.surface.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.04f), tileShape)
            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.menu_device).uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.4.sp,
                color = dimColor,
                modifier = Modifier.weight(1f),
            )
            if (isJank != null) {
                val color = if (isJank) AppTheme.color.primary else smoothColor
                Spacer(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                Text(
                    text = stringResource(if (isJank) R.string.menu_jank else R.string.menu_smooth),
                    fontSize = 9.sp,
                    letterSpacing = 0.sp,
                    color = dimColor,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            performance.forEach { meter -> Meter(meter, modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Meter(meter: VOPerformance, modifier: Modifier) {
    val color = meter.status.toComposeColor()
    Column(modifier = modifier) {
        Text(text = meter.kind.name, fontSize = 9.sp, letterSpacing = 1.sp, color = dimColor)
        Text(
            text = buildAnnotatedString {
                append(meter.value.toString())
                val unit = meter.kind.unit()
                if (unit.isNotEmpty()) withStyle(SpanStyle(fontSize = 11.sp, color = dimColor)) { append(" $unit") }
            },
            fontFamily = icelandFont,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            color = AppTheme.color.textColor,
            maxLines = 1,
        )
        Sparkline(points = meter.points, color = color, modifier = Modifier.padding(top = 2.dp).fillMaxWidth().height(18.dp))
    }
}

@Composable
private fun Sparkline(points: List<Float>, color: Color, modifier: Modifier) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val step = size.width / (points.size - 1)
        val line = Path()
        points.forEachIndexed { index, point ->
            val x = index * step
            val y = size.height - 1.dp.toPx() - point * (size.height - 2.dp.toPx())
            if (index == 0) line.moveTo(x, y) else line.lineTo(x, y)
        }
        val area = Path().apply {
            addPath(line)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(area, color.copy(alpha = 0.14f))
        drawPath(line, color, style = Stroke(width = 1.4.dp.toPx()))
    }
}

private fun VOPerformance.Kind.unit(): String = when (this) {
    VOPerformance.Kind.CPU -> "%"
    VOPerformance.Kind.RAM -> "MB"
    VOPerformance.Kind.FPS -> ""
}
