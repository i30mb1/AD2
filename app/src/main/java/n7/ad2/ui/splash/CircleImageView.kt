package n7.ad2.ui.splash

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import coil.ImageLoader
import coil.request.ImageRequest
import n7.ad2.utils.extension.toPx

class CircleImageView(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {

    companion object {
        private const val DEFAULT_SIZE = 40
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
    }
    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }
    private val viewRect = Rect()
    private var drawInitials = true

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onDraw(canvas: Canvas) {
        if (drawInitials) {
            drawInitials(canvas)
        } else {
            drawAvatar(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (width == 0) return
        viewRect.set(0, 0, width, height)
        initialsPaint.textSize = height * 0.5F
//        prepareShader(width, height)
    }

    fun avatar(initials: String, avatarPath: String) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(avatarPath)
            .target { drawable -> prepareShader(drawable) }
            .build()
        val disposable = imageLoader.enqueue(request)
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), backgroundPaint)

        val offsetY = (initialsPaint.descent() + initialsPaint.ascent()) / 2
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initialsPaint)
    }

    private fun prepareShader(drawable: Drawable) {
        val srcBm = drawable.toBitmap()
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        drawInitials = false
        invalidate()
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> DEFAULT_SIZE.toPx
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

}