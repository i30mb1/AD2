package n7.ad2.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import n7.ad2.R
import n7.ad2.utils.extension.themeColor
import n7.ad2.utils.extension.toDp

class SelectableImageView(
        context: Context,
        attributeSet: AttributeSet
) : AppCompatImageView(context, attributeSet) {

    private val borderWidth = 8.toDp
    private val rect = Rect()
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        color = context.themeColor(R.attr.colorAccent)
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isSelected) canvas?.drawRect(rect, borderPaint)
    }

}
