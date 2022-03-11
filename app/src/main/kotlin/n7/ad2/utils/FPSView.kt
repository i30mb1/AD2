package n7.ad2.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.DynamicLayout
import android.text.Editable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Choreographer
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.google.android.material.color.MaterialColors
import n7.ad2.drawer.R
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.spToPx

class FPSView(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs) {

    companion object {
        private const val FPS_INTERVAL_TIME = 1000L
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = ResourcesCompat.getFont(context, R.font.iceland_normal)
        color = MaterialColors.getColor(this@FPSView, android.R.attr.textColorPrimary)
        textSize = 14f.spToPx
    }
    private var textLayout: Layout? = null
    private var editable: Editable = SpannableStringBuilder()
    private var count = 0
    private var isFpsOpen = false
    private val fpsRunner by lazyUnsafe { FpsRunnable() }
    private val mainHandler by lazyUnsafe { Handler(Looper.getMainLooper()) }
    private val callback: (Int) -> Unit = { fps ->
        editable.clear()
        editable.append("$fps fps")
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isFpsOpen) {
            isFpsOpen = true
            mainHandler.postDelayed(fpsRunner, FPS_INTERVAL_TIME)
            Choreographer.getInstance().postFrameCallback(fpsRunner)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        count = 0
        mainHandler.removeCallbacks(fpsRunner)
        Choreographer.getInstance().removeFrameCallback(fpsRunner)
        isFpsOpen = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == oldw) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            textLayout = DynamicLayout.Builder.obtain(editable, textPaint, w).setAlignment(Layout.Alignment.ALIGN_OPPOSITE).build()
        } else {
            textLayout = DynamicLayout(editable, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.withTranslation(x = -paddingEnd.toFloat(), y = paddingTop.toFloat()) {
            textLayout?.draw(canvas)
        }
    }

    private inner class FpsRunnable : Choreographer.FrameCallback, Runnable {

        override fun doFrame(frameTimeNanos: Long) {
            count++
            Choreographer.getInstance().postFrameCallback(this)
        }

        override fun run() {
            callback.invoke(count)
            count = 0
            mainHandler.postDelayed(this, FPS_INTERVAL_TIME)
        }

    }

}