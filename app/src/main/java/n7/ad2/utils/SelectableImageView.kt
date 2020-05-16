package n7.ad2.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import n7.ad2.R
import n7.ad2.utils.extension.themeColor
import n7.ad2.utils.extension.toPx

class SelectableImageView(
        context: Context,
        attributeSet: AttributeSet
) : AppCompatImageView(context, attributeSet) {

    private val borderWidth = 8.toPx
    private val rect = Rect()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        color = context.themeColor(R.attr.colorAccent)
    }
    private var canvasHeight = 0f
    private var canvasWidth = 0f
    private var canvasHalfHeight = 0f
    private var canvasHalfWidth = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, width, height)

        canvasHeight = height.toFloat()
        canvasWidth = width.toFloat()
        canvasHalfHeight = canvasHeight / 2f
        canvasHalfWidth = canvasWidth / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isSelected) canvas?.drawRect(rect, borderPaint)
    }

}
