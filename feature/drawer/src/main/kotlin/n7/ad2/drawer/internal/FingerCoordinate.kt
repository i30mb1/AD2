package n7.ad2.drawer.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.DynamicLayout
import android.text.Editable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.google.android.material.color.MaterialColors
import n7.ad2.drawer.R
import n7.ad2.ktx.dpToPx
import n7.ad2.ktx.spToPx

internal class FingerCoordinate(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs) {

    companion object {
        private val OFFSET_START = 3.dpToPx
    }

    private val coordinateArray = arrayOfNulls<String>(10)
    private val builder = StringBuilder(14)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = ResourcesCompat.getFont(context, R.font.iceland_normal)
        color = MaterialColors.getColor(this@FingerCoordinate, android.R.attr.textColorPrimary)
        textSize = 14f.spToPx
    }
    private var textLayout: Layout? = null
    private var editable: Editable = SpannableStringBuilder()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == oldw) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            textLayout = DynamicLayout.Builder
                .obtain(editable, textPaint, w)
                .build()
        } else {
            textLayout = DynamicLayout(editable, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.withTranslation(x = paddingStart.toFloat() + OFFSET_START, y = paddingTop.toFloat()) {
            textLayout?.draw(canvas)
        }
    }

    fun handleGlobalEvent(event: MotionEvent) {
        val action = event.actionMasked
        var index = event.actionIndex
        var pointerID = event.getPointerId(index)
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> setCoordinateForPoint(pointerID, event.getX(index), event.getY(index))
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> updateCoordinate(pointerID, null)
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                index = 0
                while (index < pointerCount) {
                    pointerID = event.getPointerId(index)
                    setCoordinateForPoint(pointerID, event.getX(index), event.getY(index))
                    index++
                }
            }
        }
    }

    private fun setCoordinateForPoint(pointerID: Int, y: Float, x: Float) {
        builder.clear()
            .append("x[")
            .append(x.toInt())
            .append("]y[")
            .append(y.toInt())
            .append("]")

        updateCoordinate(pointerID, builder.toString())
    }

    private fun updateCoordinate(pointerID: Int, text: String?) {
        editable.clear()
        coordinateArray[pointerID] = text
        coordinateArray
            .forEach { coordinate ->
                if (coordinate != null) {
                    editable.append(coordinate)
                    editable.append('\n')
                }
            }
        invalidate()
    }

}