package n7.ad2.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.color.MaterialColors
import n7.ad2.core.ui.R
import kotlin.math.roundToInt

class SelectableImageView(context: Context, attributeSet: AttributeSet) : AppCompatImageView(context, attributeSet) {

    private val borderWidth = resources.getDimension(R.dimen.line_width)
    private var currentBorderWidth = 0f
    private val rect = Rect()
    private val _borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        color = MaterialColors.getColor(this@SelectableImageView, com.google.android.material.R.attr.colorPrimary)
    }
    private var canvasHeight = 0f
    private var canvasWidth = 0f
    private var canvasHalfHeight = 0f
    private var canvasHalfWidth = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, borderWidth.roundToInt(), height)

        canvasHeight = height.toFloat()
        canvasWidth = width.toFloat()
        canvasHalfHeight = canvasHeight / 2f
        canvasHalfWidth = canvasWidth / 2f
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        ValueAnimator.ofFloat(currentBorderWidth, if (selected) borderWidth else 0f).apply {
            duration = resources.getInteger(R.integer.animation_medium).toLong()
            addUpdateListener {
                currentBorderWidth = it.animatedValue as Float
                rect.set(0, 0, currentBorderWidth.toInt(), height)
                postInvalidateOnAnimation()
            }
            start()
        }
    }

//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//        if (currentBorderWidth != 0f) canvas?.drawRect(rect, borderPaint)
//    }
}
