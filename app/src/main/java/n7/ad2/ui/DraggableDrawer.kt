package n7.ad2.ui

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
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.FragmentContainerView
import n7.ad2.R
import n7.ad2.utils.extension.toPx
import kotlin.math.abs

class DraggableDrawer(
    context: Context,
    attributeSet: AttributeSet,
) : FrameLayout(context, attributeSet) {

    companion object {
        private val offsetRight = 130.toPx
        private const val minScale = 0.6f
        private const val maxScale = 1.0f
    }

    private lateinit var draggableView: FragmentContainerView
    private val hitRect = Rect()
    private var initialMotionX = 0F
    private var isCollapsed: Boolean = false
    private var isDragViewInit = false
    private var isIntercept = false
    private var currentScale = minScale
    private var offsetX = 0
    private var offsetY = 0
    private val onAnimationEnd: (() -> Unit)? = null

    private val dragHelper = ViewDragHelper.create(this, 1F, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int) = child == draggableView
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = MathUtils.clamp(left, width - child.width, width - child.width + offsetRight)
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int) = 0
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) = onReleased(xvel)
        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val scale = maxScale - left.toFloat() / offsetRight * (maxScale - minScale)
            draggableView.scaleY = scale
            draggableView.scaleX = scale
            currentScale = draggableView.scaleX
            offsetX = changedView.left
            offsetY = changedView.top
        }
    })

    init {
        context.withStyledAttributes(attributeSet, R.styleable.DrawableDrawer) {
            isCollapsed = getBooleanOrThrow(R.styleable.DrawableDrawer_isCollapsed)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (child is FragmentContainerView) draggableView = child
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initDragView()
        draggableView.offsetLeftAndRight(offsetX)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        draggableView.getHitRect(hitRect)
        val clickedOnView = hitRect.contains(ev.x.toInt(), ev.y.toInt())
        if (clickedOnView) {
            dragHelper.processTouchEvent(ev)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialMotionX = ev.x
                    isIntercept = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentSlopX = abs(ev.x - initialMotionX)
                    if (currentSlopX > dragHelper.touchSlop) isIntercept = true
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

    override fun performClick(): Boolean = super.performClick()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        performClick()
        return super.onTouchEvent(event)
    }

    private fun onReleased(xVel: Float) {
        if (!isIntercept) return
        val finalStateIsCollapsed = xVel > 0
        val finalX = if (finalStateIsCollapsed) offsetRight else 0
        val startSettling = dragHelper.smoothSlideViewTo(draggableView, finalX, 0)
        if (startSettling) SettleRunnable().run()
    }

    private fun initDragView() {
        if (isDragViewInit) return
        if (isCollapsed) {
            offsetX = offsetRight
            draggableView.scaleY = minScale
            draggableView.scaleX = minScale
            currentScale = draggableView.scaleX

        }
        isDragViewInit = true
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