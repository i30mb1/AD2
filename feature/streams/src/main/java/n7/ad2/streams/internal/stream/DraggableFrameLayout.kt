package n7.ad2.streams.internal.stream

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

private data class Point(val x: Float, val y: Float)

class DraggableFrameLayout(
    context: Context,
    attributeSet: AttributeSet,
) : FrameLayout(context, attributeSet) {

    companion object {
        const val MAX_SCALE = 1F
        const val MIN_SCALE = 0.5F
        const val POSITIVE_VELOCITY_FOR_STICK_TO_BORDER = 2000
        const val NEGATIVE_VELOCITY_FOR_STICK_TO_BORDER = -2000
    }

    private lateinit var draggableView: View
    private var interceptKeyEvents = false
    private var offsetX = 0
    private var offsetY = 0
    private var initialMotionX = 0F
    private var initialMotionY = 0F
    private var finalX = 0
    private var finalY = 0
    private var currentScale = MAX_SCALE
    private var closeCallBack = { }
    private val dragHelper = ViewDragHelper.create(this, 1F, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int) = child == draggableView
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = left
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = top
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) = onReleased(releasedChild, xvel, yvel)
        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            offsetX = changedView.left - changedView.marginLeft
            offsetY = changedView.top - changedView.marginTop
        }
    })
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            currentScale *= detector.scaleFactor
            currentScale = MathUtils.clamp(currentScale, MIN_SCALE, MAX_SCALE)

            draggableView.pivotY = draggableView.height / 2f
            draggableView.pivotX = draggableView.width / 2f
            draggableView.scaleX = currentScale
            draggableView.scaleY = currentScale
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        draggableView.offsetLeftAndRight(offsetX)
        draggableView.offsetTopAndBottom(offsetY)
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (child is View) draggableView = child
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        dragHelper.processTouchEvent(event)
        if (interceptKeyEvents) return true
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val viewRect = Rect()
        draggableView.getHitRect(viewRect)
        val clickedOnView = viewRect.contains(ev.x.toInt(), ev.y.toInt())
        interceptKeyEvents = false
        if (clickedOnView) {
            dragHelper.processTouchEvent(ev)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialMotionX = ev.x
                    initialMotionY = ev.y
                    interceptKeyEvents = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentSlopX = abs(ev.x - initialMotionX)
                    val currentSlopY = abs(ev.y - initialMotionY)
                    if (currentSlopX > dragHelper.touchSlop || currentSlopY > dragHelper.touchSlop) interceptKeyEvents = true
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    dragHelper.cancel()
                    interceptKeyEvents = false
                }
            }
            return interceptKeyEvents
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun onReleased(child: View, xVel: Float, yVel: Float) {
        var (x, y) = getLocation(child)
        x += (child.measuredWidth * currentScale) / 2
        y -= (child.measuredHeight * currentScale) / 2
        finalX = when {
            x < width / 2 -> stickToStart(child)
            else -> stickToEnd(child)
        }
        finalY = when {
            y < height / 2 -> stickToTop(child)
            else -> stickToBottom(child)
        }
//        dragHelper.smoothSlideViewTo(child, finalX, finalY)
    }

    private fun getLocation(view: View): Point {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return Point(location[0].toFloat(), location[1].toFloat())
    }

    private fun stickToBottom(child: View): Int {
        return height - child.marginBottom - child.measuredHeight + getHeightDifference(child)
    }

    private fun stickToTop(child: View): Int {
        return child.marginTop - getHeightDifference(child)
    }

    private fun stickToEnd(child: View): Int {
        return (width - child.marginEnd - child.measuredWidth + getWidthDifference(child)).let {
            if (it < child.marginStart) child.marginStart else it // reset to leftmost position if out of bound
        }
    }

    private fun stickToStart(child: View): Int {
        return child.marginStart - getWidthDifference(child)
    }

    private fun getHeightDifference(child: View): Int {
        return (child.measuredHeight - child.measuredHeight * currentScale).toInt() / 2
    }

    private fun getWidthDifference(child: View): Int {
        return (child.measuredWidth - child.measuredWidth * currentScale).toInt() / 2
    }

    private inner class SettleRunnable(private val view: View, private val onAnimationEnd: (() -> Unit)? = null) : Runnable {
        override fun run() {
            if (dragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(view, this)
            } else {
                onAnimationEnd?.invoke()
            }
        }
    }

}