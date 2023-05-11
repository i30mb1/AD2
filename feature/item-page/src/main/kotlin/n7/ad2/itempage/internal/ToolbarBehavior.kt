package n7.ad2.itempage.internal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import n7.ad2.feature.itempage.R

// https://proandroiddev.com/complex-ui-animation-on-android-8f7a46f4aec4
class ToolbarBehavior(context: Context, attributeSet: AttributeSet? = null) : CoordinatorLayout.Behavior<AppBarLayout>(context, attributeSet) {

    private lateinit var toolbar: View
    private lateinit var toolbarTitle: View

    private var toolbarOriginalHeight: Float = -1f
    private var toolbarCollapsedHeight: Float = -1f
    private var viewsSet = false
    private var minScale = 0.6f

    private fun getViews(child: AppBarLayout) {
        if (viewsSet) return
        viewsSet = true

        toolbar = child.findViewById(R.id.toolbar)
        toolbarTitle = child.findViewById(R.id.title)

        toolbarOriginalHeight = toolbar.layoutParams.height.toFloat()
        toolbarCollapsedHeight = toolbarOriginalHeight * minScale
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        getViews(child)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(cl: CoordinatorLayout, child: AppBarLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(cl, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        getViews(child)

        if (dyConsumed > 0) {
            // scroll up
            if (toolbar.layoutParams.height > toolbarCollapsedHeight) {
                // --- shrink toolbar
                val height = toolbar.layoutParams.height - dyConsumed
                toolbar.layoutParams.height = if (height < toolbarCollapsedHeight) toolbarCollapsedHeight.toInt() else height
                toolbar.requestLayout()
                // --- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                toolbarTitle.scaleX = if (scale < minScale) minScale else scale
                toolbarTitle.scaleY = toolbarTitle.scaleX
            }
        } else if (dyUnconsumed < 0) {
            // scroll down
            if (toolbar.layoutParams.height < toolbarOriginalHeight) {
                // --- expand toolbar
                // subtract because dyUnconsumed is < 0
                val height = toolbar.layoutParams.height - dyUnconsumed
                toolbar.layoutParams.height = if (height > toolbarOriginalHeight) toolbarOriginalHeight.toInt() else height
                toolbar.requestLayout()
                // --- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                toolbarTitle.scaleX = if (scale < minScale) minScale else scale
                toolbarTitle.scaleY = toolbarTitle.scaleX
            }
        }
    }

}
