package n7.ad2.utils

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.withTranslation
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

// https://youtu.be/xF1x-Pm6IPw
class StickyHeaderDecorator(
    private val recyclerView: RecyclerView,
    private val listener: StickyHeaderInterface,
) : ItemDecoration() {

    private var mStickyHeaderHeight = 0
    private val header: ViewDataBinding by lazy {
        val layoutInflater = LayoutInflater.from(recyclerView.context)
        DataBindingUtil.inflate(layoutInflater, listener.getHeaderLayout(), recyclerView, false)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        val currentHeader = getHeaderViewForItem(topChildPosition)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint) ?: return
        if (listener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact)
            return
        }
        currentHeader.draw(c)
    }

    private fun getHeaderViewForItem(itemPosition: Int): View {
        val headerPosition = getHeaderPositionForItem(itemPosition)
        listener.bindHeaderData(header, headerPosition)
        return header.root
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.withTranslation(0f, nextHeader.top - currentHeader.height.toFloat()) {
            currentHeader.draw(this)
        }
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        parent.forEach {
            if (it.bottom > contactPoint && it.top <= contactPoint) return it
        }
        return null
    }

    /**
     * Properly measures and layouts the top sticky header.
     * https://stackoverflow.com/questions/50878305/how-to-draw-a-view-in-an-itemdecoration/50919862
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight.also { mStickyHeaderHeight = it })
    }

    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        while (position >= 0) {
            if (listener.isHeader(position)) return position
            position--
        }
        return 0
    }

    interface StickyHeaderInterface {
        /**
         * This method gets called by [StickyHeaderDecorator] to get layout resource id for the header item at specified adapter's position.
         *
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(): Int

        /**
         * This method gets called by [StickyHeaderDecorator] to setup the header View.
         *
         * @param header         View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: ViewDataBinding, headerPosition: Int)

        /**
         * This method gets called by [StickyHeaderDecorator] to verify whether the item represents a header.
         *
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(position: Int): Boolean
    }

    init {
        // On Sticky Header Click
        recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                // Handle the clicks on the header here ...
                return motionEvent.y <= mStickyHeaderHeight
            }

            override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }
}