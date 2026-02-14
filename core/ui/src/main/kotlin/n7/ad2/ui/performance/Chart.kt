package n7.ad2.ui.performance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compose version of ChartView with improved performance and modern architecture
 */
@Composable
fun Chart(state: ChartState, modifier: Modifier = Modifier, lineColor: Color = Color.Red, textColor: Color = Color.White, gridColor: Color = Color.Gray, lineWidth: Float = 2f, textSize: Float = 12f) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val startOffsetPx = remember { with(density) { 52.dp.toPx() } }
    val textOffsetPx = remember { with(density) { 4.dp.toPx() } }
    val lineWidthPx = remember { with(density) { lineWidth.dp.toPx() } }
    val gridWidthPx = remember { with(density) { 0.5.dp.toPx() } }

    val textStyle = remember {
        TextStyle(
            color = textColor,
            fontSize = textSize.sp,
            fontWeight = FontWeight.Normal,
        )
    }

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        if (state.values.isEmpty()) return@Canvas

        drawChart(
            state = state,
            lineColor = lineColor,
            gridColor = gridColor,
            startOffsetPx = startOffsetPx,
            textOffsetPx = textOffsetPx,
            lineWidthPx = lineWidthPx,
            gridWidthPx = gridWidthPx,
            textStyle = textStyle,
            textMeasurer = textMeasurer,
        )
    }
}

private fun DrawScope.drawChart(
    state: ChartState,
    lineColor: Color,
    gridColor: Color,
    startOffsetPx: Float,
    textOffsetPx: Float,
    lineWidthPx: Float,
    gridWidthPx: Float,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
) {
    // Calculate drawing area
    val chartWidth = size.width - startOffsetPx
    val chartHeight = size.height

    // Create paths for line and gradient
    val linePath = createLinePath(state, chartWidth, chartHeight, startOffsetPx)
    val gradientPath = createGradientPath(linePath, chartWidth, chartHeight, startOffsetPx)

    // Draw gradient fill
    drawGradient(gradientPath, state, startOffsetPx)

    // Draw line
    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(
            width = lineWidthPx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )

    // Draw grid
    drawGrid(gridColor, gridWidthPx, startOffsetPx)

    // Draw scales
    drawScales(state.maxValue, startOffsetPx, textOffsetPx, textStyle, textMeasurer)
}

private fun createLinePath(state: ChartState, chartWidth: Float, chartHeight: Float, startOffsetPx: Float): Path {
    val path = Path()
    val pointWidth = chartWidth / state.values.size

    var prevX = startOffsetPx + pointWidth / 2
    var prevY = chartHeight - chartHeight * state.values[0].value / state.maxValue.toFloat()

    path.moveTo(prevX, prevY)

    for (i in 1..state.values.lastIndex) {
        val valueFactor = state.values[i].value / state.maxValue.toFloat()
        val x = startOffsetPx + i * pointWidth + pointWidth / 2
        val y = chartHeight - chartHeight * valueFactor

        // Create smooth curve using cubic bezier
        path.cubicTo(
            (x + prevX) / 2,
            prevY,
            (x + prevX) / 2,
            y,
            x,
            y,
        )
        prevX = x
        prevY = y
    }

    return path
}

private fun createGradientPath(linePath: Path, chartWidth: Float, chartHeight: Float, startOffsetPx: Float): Path {
    val gradientPath = Path()
    gradientPath.addPath(linePath)

    // Close the path by drawing to bottom corners
    gradientPath.lineTo(startOffsetPx + chartWidth, chartHeight)
    gradientPath.lineTo(startOffsetPx, chartHeight)
    gradientPath.close()

    return gradientPath
}

private fun DrawScope.drawGradient(gradientPath: Path, state: ChartState, startOffsetPx: Float) {
    val colors = state.values.map { it.color }

    val brush = if (colors.size < 2) {
        Brush.linearGradient(
            colors = listOf(colors.first(), colors.first()),
            start = Offset(startOffsetPx, 0f),
            end = Offset(size.width, 0f),
        )
    } else {
        Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetPx, 0f),
            end = Offset(size.width, 0f),
        )
    }

    drawPath(
        path = gradientPath,
        brush = brush,
    )
}

private fun DrawScope.drawGrid(gridColor: Color, gridWidthPx: Float, startOffsetPx: Float) {
    val scaleCount = 4
    val count = scaleCount - 1
    val widthUnit = size.width / count
    val heightUnit = size.height / count

    // Draw horizontal grid lines
    repeat(count) { index ->
        drawLine(
            color = gridColor,
            start = Offset(startOffsetPx, heightUnit * (index + 1f)),
            end = Offset(size.width, heightUnit * (index + 1f)),
            strokeWidth = gridWidthPx,
        )
    }

    // Draw vertical grid lines
    repeat(count + 1) { index ->
        drawLine(
            color = gridColor,
            start = Offset(widthUnit * index + startOffsetPx, 0f),
            end = Offset(widthUnit * index + startOffsetPx, size.height),
            strokeWidth = gridWidthPx,
        )
    }
}

private fun DrawScope.drawScales(maxValue: Int, startOffsetPx: Float, textOffsetPx: Float, textStyle: TextStyle, textMeasurer: TextMeasurer) {
    val scaleCount = 4

    for (index in 1 until scaleCount) {
        val scaleValue = (maxValue * index / (scaleCount - 1).toFloat()).toInt().toString()
        val textResult = textMeasurer.measure(scaleValue, textStyle)

        val y = lerp(size.height, textResult.size.height / 2f, index / (scaleCount - 1).toFloat())

        drawText(
            textMeasurer = textMeasurer,
            text = scaleValue,
            style = textStyle,
            topLeft = Offset(
                x = startOffsetPx - textResult.size.width - textOffsetPx,
                y = y - textResult.size.height / 2,
            ),
        )
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + fraction * (stop - start)

/**
 * State class for Chart composable
 */
data class ChartState(val values: List<ChartValue>, val maxValue: Int) {
    data class ChartValue(val value: Int, val color: Color)
}
