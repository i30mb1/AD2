package n7.ad2.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import n7.ad2.R
import n7.ad2.utils.extension.themeColor

class SelectableImageView(
        context: Context,
        attributeSet: AttributeSet
) : AppCompatImageView(context, attributeSet) {

    private val borderWidth = resources.getDimension(R.dimen.line_width)
    private var currentBorderWidth = 0f
    private val rect = Rect()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        color = context.themeColor(R.attr.colorAccent)
    }
    private var canvasHeight = 0f
    private var canvasWidth = 0f
    private var canvasHalfHeight = 0f
    private var canvasHalfWidth = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, borderWidth.toInt(), height)

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (currentBorderWidth != 0f) canvas?.drawRect(rect, borderPaint)
    }

}
