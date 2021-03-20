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
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import n7.ad2.R
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
    var initialsOffsetY = 0F
    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }
    private val viewRect = Rect()
    private val iconRect = Rect()
    private val groupIcon = ResourcesCompat.getDrawable(resources, R.drawable.creep, null)!!
    private val personIcon = ResourcesCompat.getDrawable(resources, R.drawable.creep2, null)!!

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
//        drawInitials(canvas)
        drawGroupIcon(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (width == 0) return
        viewRect.set(0, 0, width, height)
        val desiredWidth = height / 2
        val iconOffset = (height - desiredWidth) / 2
        iconRect.set(iconOffset, iconOffset, width - iconOffset, height - iconOffset)
        initialsPaint.textSize = height * 0.5F
        initialsOffsetY = (initialsPaint.descent() + initialsPaint.ascent()) / 2
//        prepareShader(width, height)
    }

    fun avatar(initials: String, avatarPath: String) {
        this.initials = initials
    }

    private fun drawGroupIcon(canvas: Canvas) {
        groupIcon.bounds = iconRect
        groupIcon.draw(canvas)
    }

    private fun drawAvatar(canvas: Canvas) {
        drawable.toBitmap()
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), backgroundPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - initialsOffsetY, initialsPaint)
    }

    private fun prepareAvatarShader(drawable: Drawable) {
        val srcBm = drawable.toBitmap()
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
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