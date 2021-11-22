package n7.ad2.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.getBooleanOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.FragmentContainerView
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.ui.MyApplication
import n7.ad2.utils.extension.toPx
import javax.inject.Inject
import kotlin.math.abs

class DraggableDrawer(
    context: Context,
    attributeSet: AttributeSet,
) : FrameLayout(context, attributeSet) {

    companion object {
        private val collapsedOffsetX = 130.toPx
        private const val collapsedScale = 0.6f
        private const val maxScale = 1.0f
        private const val defaultElevation = 10f
        private const val POSITIVE_VELOCITY_FOR_STICK_TO_BORDER = 1000
        private const val NEGATIVE_VELOCITY_FOR_STICK_TO_BORDER = -1000
    }

    interface Listener {
        fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?)
    }

    var isOpen: Boolean = false
    @Inject lateinit var logger: AD2Logger
    private lateinit var draggableView: FragmentContainerView
    private var touchSlop = 0
    private var initialMotionX = 0F
    private var isCollapsed: Boolean = false
    private var isDraggableViewInitiated = false
    private var isIntercept = false
    private var offsetX = 0
    private var offsetY = 0
    private var currentOffsetX = 0
    private var navigationBarsInsetsBottom: Int = 0
    private var statusBarsInsetsTop: Int = 0
    private var onAnimationEnd: (() -> Unit)? = null
    private var drawerPercent: ((percent: Float) -> Unit)? = null
    private val draggableRect = Rect()

    private val dragHelper = ViewDragHelper.create(this, 1F, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int) = child == draggableView
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = MathUtils.clamp(left, width - child.width, width - child.width + collapsedOffsetX)
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int) = 0
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) = onReleased(xvel)
        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val percent = 1 - (left.toFloat()) / (collapsedOffsetX)
            drawerPercent?.invoke(percent)
            val scale = maxScale - (left.toFloat()) / (collapsedOffsetX) * (maxScale - collapsedScale)
            draggableView.scaleY = scale
            draggableView.scaleX = scale
            offsetX = changedView.left
            offsetY = changedView.top
            currentOffsetX = offsetX
        }
    })

    init {
        (context.applicationContext as MyApplication).component.inject(this)
        context.withStyledAttributes(attributeSet, R.styleable.DrawableDrawer) {
            isCollapsed = getBooleanOrThrow(R.styleable.DrawableDrawer_isCollapsed)
        }
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            navigationBarsInsetsBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            statusBarsInsetsTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            insets
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (child is FragmentContainerView) draggableView = child
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initDraggableView()
        draggableView.offsetLeftAndRight(offsetX)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        draggableView.getHitRect(draggableRect)
        val clickedOnView = draggableRect.contains(ev.x.toInt(), ev.y.toInt())
        if (clickedOnView) {
            dragHelper.processTouchEvent(ev)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialMotionX = ev.x
                    isIntercept = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentSlopX = abs(ev.x - initialMotionX)
                    if (currentSlopX > touchSlop) {
                        isIntercept = true
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    dragHelper.cancel()
                    isIntercept = false
                }
            }
            return isIntercept
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        performClick()
        return super.onTouchEvent(event)
    }

    fun close() {
        val startSettling = dragHelper.smoothSlideViewTo(draggableView, collapsedOffsetX, 0)
        if (startSettling) SettleRunnable().run()
        isOpen = false
    }

    fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?) {
        drawerPercent = listener
    }

    private fun onReleased(xVel: Float) {
//        if (!isIntercept) return
        val finalX = when {
            xVel < NEGATIVE_VELOCITY_FOR_STICK_TO_BORDER -> 0
            xVel > POSITIVE_VELOCITY_FOR_STICK_TO_BORDER -> collapsedOffsetX
            currentOffsetX < collapsedOffsetX / 2 -> 0
            else -> collapsedOffsetX
        }
        val startSettling = dragHelper.smoothSlideViewTo(draggableView, finalX, 0)
        if (startSettling) SettleRunnable().run()
        isOpen = finalX != collapsedOffsetX
    }

    private fun initDraggableView() {
        if (isDraggableViewInitiated) return
        if (isCollapsed) {
            offsetX = collapsedOffsetX
            draggableView.scaleY = collapsedScale
            draggableView.scaleX = collapsedScale
        }
        touchSlop = dragHelper.touchSlop
        draggableView.elevation = defaultElevation
        isDraggableViewInitiated = true
    }

    private inner class SettleRunnable : Runnable {
        override fun run() {
            if (dragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(draggableView, this)
            } else {
                onAnimationEnd?.invoke()
            }
        }
    }

}