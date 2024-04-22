package n7.ad2.ui.performance

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.util.lerp
import kotlin.math.ceil
import n7.ad2.ktx.dpToPx

internal class ChartView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val matrix: Matrix = Matrix()
    private val scaleMatrix: Matrix = Matrix()

    private val linePaint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f.dpToPx
        isAntiAlias = true
    }

    private val textPaint: TextPaint = TextPaint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 12f.dpToPx
    }

    private val gradientPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val gridPaint: Paint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
        strokeWidth = 0.5f.dpToPx
    }
    private var state: State? = null
    private val linePath: Path = Path()
    private val gradientPath: Path = Path()
    private val startOffset = 52f.dpToPx
    private val textOffset = 4f.dpToPx
    private val textHeight = textPaint.fontMetrics.run { ceil(descent - ascent) }
    private val scales: Array<String> = Array(SCALE_COUNT) { "" }
    private var gradientColors: IntArray = IntArray(0)

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val state = state?.takeIf { it.values.isNotEmpty() } ?: return
        val pointWidth = width.toFloat() / state.values.size
        scaleMatrix.reset()
        scaleMatrix.setScale(
            (width - startOffset) / (width - pointWidth),
            1f,
            width.toFloat(),
            height.toFloat(),
        )
        scaleMatrix.preTranslate(pointWidth / 2, 0f)

        fillLine(state, pointWidth)
        fillGradient(state, pointWidth)

        gradientPath.transform(scaleMatrix)
        linePath.transform(scaleMatrix)

        canvas.drawPath(linePath, linePaint)
        canvas.drawPath(gradientPath, gradientPaint)

        drawGrid(canvas)
        drawScales(state.maxValue, canvas)
    }

    fun render(state: State) {
        this.state = state
        refillGradient(state)
        invalidate()
    }

    private fun refillGradient(state: State) {
        if (state.values.size == gradientColors.size) {
            for (i in 0..gradientColors.lastIndex) {
                gradientColors[i] = resources.getColor(state.values[i].color, null)
            }
        } else {
            gradientColors = IntArray(state.values.size) { index ->
                resources.getColor(state.values[index].color, null)
            }
        }
    }

    private fun fillGradient(state: State, pointWith: Float) {
        gradientPath.reset()
        gradientPath.addPath(linePath)

        gradientPath.lineTo(width.toFloat() - (pointWith / 2), height.toFloat())
        gradientPath.lineTo((pointWith / 2), height.toFloat())
        gradientPath.transform(matrix, linePath)
        gradientPath.close()

        if (state.values.size < 2) {
            gradientPaint.color = gradientColors[0]
            return
        } else {
            gradientPaint.shader = LinearGradient(
                startOffset,
                0f,
                width.toFloat(),
                0f,
                gradientColors,
                null,
                Shader.TileMode.CLAMP,
            )
        }
    }

    private fun fillLine(state: State, pointWith: Float) {
        linePath.reset()

        var prevX = pointWith / 2
        var prevY = height - height * state.values[0].value / state.maxValue.toFloat()

        linePath.moveTo(prevX, prevY)

        for (i in 1..state.values.lastIndex) {
            val valueFactor = state.values[i].value / state.maxValue.toFloat()
            val x = i * pointWith + pointWith / 2
            val y = height - height * valueFactor

            linePath.cubicTo((x + prevX) / 2, prevY, (x + prevX) / 2, y, x, y)
            prevX = x
            prevY = y
        }
    }

    private fun drawScales(maxValue: Int, canvas: Canvas) {
        for (index in 0..scales.lastIndex) {
            scales[index] = (maxValue * index / scales.lastIndex.toFloat()).toInt().toString()
        }
        for (index in 1..scales.lastIndex) {
            val textWidth = StaticLayout.getDesiredWidth(scales[index], textPaint)
            canvas.drawText(
                scales[index],
                startOffset - textWidth - textOffset,
                lerp(height.toFloat(), 0f, index / scales.lastIndex.toFloat()) + textHeight / (SCALE_COUNT - 1),
                textPaint,
            )
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val count = SCALE_COUNT - 1
        val widthUnit = width.toFloat() / count
        val heightUnit = height.toFloat() / count

        repeat(count) { index ->
            canvas.drawLine(
                startOffset,
                heightUnit * (index + 1f),
                width.toFloat(),
                heightUnit * (index + 1f),
                gridPaint
            )
        }

        repeat(count + 1) { index ->
            canvas.drawLine(
                widthUnit * index + startOffset,
                0f,
                widthUnit * index + startOffset,
                height.toFloat(),
                gridPaint
            )
        }
    }

    companion object {
        private const val SCALE_COUNT = 4
    }

    class State(
        val values: List<ColoredValue>,
        val maxValue: Int,
    ) {
        class ColoredValue(
            val value: Int,
            val color: Int,
        )
    }
}
