package n7.ad2.ui.frameCounter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.animation.doOnEnd
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import n7.ad2.core.ui.R
import n7.ad2.core.ui.databinding.WidgetInfoBinding
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ktx.dpToPx
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ui.performance.ChartsOwner
import n7.ad2.ui.performance.PollerImpl
import n7.ad2.ui.performance.ResourceUsage

class WindowOverlay(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val dispatcher: DispatchersProvider,
    private val scope: CoroutineScope,
) : DefaultLifecycleObserver {

    private val windowManager by lazyUnsafe { context.getSystemService<WindowManager>()!! }
    private var viewOwner: ViewOwner? = null
    private var onDoubleTapListener: (() -> Unit)? = null
    private val poller = PollerImpl(context, lifecycle)

    init {
        setEnable(true)
        poller.usage
            .onEach { list -> render(list) }
            .launchIn(scope)
    }

    private fun setEnable(enable: Boolean): Boolean {
        if (enable) {
            if (!requestPermission()) {
                return false
            }
            val viewOwner = ViewOwner(
                scope,
                context,
                windowManager,
                dispatcher,
            )
            this.viewOwner = viewOwner
            windowManager.addView(viewOwner.container, viewOwner.layoutParams)
            lifecycle.addObserver(this)
        } else {
            lifecycle.removeObserver(this)
            viewOwner?.animate(show = false) {
                windowManager.removeView(viewOwner?.container)
            }

            viewOwner = null
        }
        return true
    }

    override fun onStart(owner: LifecycleOwner) {
        viewOwner?.animate(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        viewOwner?.animate(false)
    }

    private suspend fun render(resourceUsage: List<ResourceUsage>) {
        viewOwner?.render(resourceUsage)
    }

    private fun requestPermission(): Boolean {
        return if (Settings.canDrawOverlays(context)) {
            true
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            false
        }
    }

}

class PanelInfoMapper {
    fun map(usage: ResourceUsage): InfoPanelView.State {
        return with(usage) {
            InfoPanelView.State(
                cpuColor = cpu.status.toColor(),
                cpuValue = cpu.value,
                ramColor = ram.status.toColor(),
                ramValue = ram.value,
                fpsColor = fps.status.toColor(),
                fpsValue = fps.value,
            )
        }
    }
}

@ColorRes
fun ResourceUsage.Status.toColor(): Int {
    return when (this) {
        ResourceUsage.Status.VERY_BAD -> R.color.performance_widget_very_bad_color
        ResourceUsage.Status.POOR -> R.color.performance_widget_poor_color
        ResourceUsage.Status.FAIR -> R.color.performance_widget_fair_color
        ResourceUsage.Status.GOOD -> R.color.performance_widget_good_color
        ResourceUsage.Status.VERY_GOOD -> R.color.performance_widget_very_good_color
        ResourceUsage.Status.EXCELLENT -> R.color.performance_widget_excellent_color
    }
}

class ViewOwner(
    scope: CoroutineScope,
    private val context: Context,
    private val windowManager: WindowManager,
    private val dispatcher: DispatchersProvider,
) {

    val layoutParams: LayoutParams = createLayoutParams()
    val container: WidgetContainerView = createContainer()

    private val infoPanelView: InfoPanelView = InfoPanelView(context)
    private val chartsOwner = ChartsOwner(container, dispatcher)
    private val mapper = PanelInfoMapper()
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = DecelerateInterpolator()
        this.duration = 2000
    }

    init {
        container.addView(infoPanelView)
    }

    suspend fun render(list: List<ResourceUsage>) {
        withContext(dispatcher.Main) {
            infoPanelView.render(mapper.map(list.last()))
            chartsOwner.render(list)
        }
    }

    fun animate(show: Boolean, onEnd: (() -> Unit)? = null) {
        animator.cancel()
        container.isVisible = true
        container.alpha = if (show) 0f else 1f
        animator.removeAllListeners()
        animator.addUpdateListener {
            container.alpha = if (show) {
                it.animatedValue as Float
            } else {
                1f - it.animatedValue as Float
            }
            animator.doOnEnd {
                container.isVisible = show
                onEnd?.invoke()
            }
        }
        animator.start()
    }

    @Suppress("DEPRECATION")
    private fun createLayoutParams(): LayoutParams {
        val width = context.resources.getDimensionPixelSize(R.dimen.performance_width)
        return LayoutParams(
            width,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (windowManager.defaultDisplay.width - width) / 2
            y = context.resources.getDimensionPixelSize(R.dimen.performance_top_offset)
        }
    }

    private fun createContainer(): WidgetContainerView {
        return WidgetContainerView(context).apply {
            clipChildren = false
            isVisible = false
            setOnTouchListener(
                WindowTouchListener(
                    this,
                    windowManager,
                    this@ViewOwner.layoutParams,
                    {},
                    {},
                )
            )
        }
    }
}

class InfoPanelView(context: Context) : LinearLayout(context) {

    private val binding: WidgetInfoBinding = WidgetInfoBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        updatePadding(
            top = 10.dpToPx,
            bottom = 10.dpToPx,
        )
    }

    fun render(state: State) {
        renderInfo(binding.cpuInfo, state.cpuColor, resources.getString(R.string.widget_cpu_format, state.cpuValue))
        renderInfo(binding.ramInfo, state.ramColor, resources.getString(R.string.widget_ram_format, state.ramValue))
        renderInfo(binding.fpsInfo, state.fpsColor, state.fpsValue.toString())
    }

    private fun renderInfo(view: TextView, color: Int, text: String) {
        view.text = text
        view.setTextColor(resources.getColor(color, null))
    }

    class State(
        @ColorRes
        val cpuColor: Int,
        val cpuValue: Int,
        @ColorRes
        val ramColor: Int,
        val ramValue: Int,
        @ColorRes
        val fpsColor: Int,
        val fpsValue: Int,
    )
}

class WidgetContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val radius: Float = 12f.dpToPx
    private val strokePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 1f.dpToPx
    }

    init {
        setBackgroundResource(R.color.gray_100)
        orientation = VERTICAL
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            radius,
            radius,
            strokePaint
        )
    }
}

class WindowTouchListener(
    private val view: View,
    private val windowManager: WindowManager,
    private val layoutParams: LayoutParams,
    private val onDoubleTap: () -> Unit,
    private val onTap: () -> Unit,
) : SimpleOnGestureListener(), View.OnTouchListener {

    private val gestureDetector = GestureDetectorCompat(view.context, this)
    private val rect: Rect = Rect()
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var diffX: Int = 0
    private var diffY: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = layoutParams.x - event.rawX
                downY = layoutParams.y - event.rawY
                true
            }

            MotionEvent.ACTION_MOVE -> {
                view.getWindowVisibleDisplayFrame(rect)
                diffX = (event.rawX + downX).toInt().coerceIn(rect.left, rect.right - view.width)
                diffY = (event.rawY + downY).toInt().coerceIn(0, rect.bottom - rect.top - view.height)
                true
            }

            else -> false
        }.also { gestureDetector.onTouchEvent(event) }
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        layoutParams.x = diffX
        layoutParams.y = diffY
        windowManager.updateViewLayout(view, layoutParams)
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleTap()
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onTap()
        return true
    }
}