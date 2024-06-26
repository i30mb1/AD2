package n7.ad2.ui.frameCounter

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.DynamicLayout
import android.text.Editable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.color.MaterialColors
import n7.ad2.core.ui.R
import n7.ad2.ktx.spToPx

class JunkView(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs) {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = ResourcesCompat.getFont(context, R.font.iceland_normal)
        color = MaterialColors.getColor(this@JunkView, android.R.attr.textColorPrimary)
        textSize = 14f.spToPx
    }
    private var editable: Editable = SpannableStringBuilder()
    private var textLayout: Layout? = null
    private var frameCounter: FrameCounter? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lifecycle = findViewTreeLifecycleOwner()?.lifecycle
        if (lifecycle != null) {
            frameCounter = JankStatsFrameCounter(lifecycle, (context as Activity).window)
            frameCounter?.isJunkCallback = { isJunk -> process(isJunk) }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == oldw) return
        textLayout = DynamicLayout.Builder.obtain(editable, textPaint, w).setAlignment(Layout.Alignment.ALIGN_OPPOSITE).build()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.withTranslation(x = -paddingEnd.toFloat(), y = paddingTop.toFloat()) {
            textLayout?.draw(canvas)
        }
    }

    private fun process(isJunk: Boolean) {
        editable.clear()
        editable.append("isJunk = $isJunk")
        invalidate()
    }

}
