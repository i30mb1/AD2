package n7.ad2.utils

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.withTranslation
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

// https://youtu.be/xF1x-Pm6IPw
class StickyHeaderDecorator<T : RecyclerView.ViewHolder>(
    private val adapter: RecyclerView.Adapter<T>,
    private val recyclerView: RecyclerView,
) : ItemDecoration() {

    interface StickyHeaderInterface {
        fun getHeaderLayout(): Int
    }

    init {
        if (adapter !is StickyHeaderInterface) throw IllegalStateException("adapter should implement ${StickyHeaderInterface::class.java.canonicalName}")
        recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                // Handle the clicks on the header here ...
                return motionEvent.y <= mStickyHeaderHeight
            }

            override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private var mStickyHeaderHeight = 0
    private val listener: StickyHeaderInterface = adapter as StickyHeaderInterface
    private val header: RecyclerView.ViewHolder by lazy {
        adapter.createViewHolder(recyclerView, listener.getHeaderLayout())
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
        if (isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact)
            return
        }
        currentHeader.draw(c)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getHeaderViewForItem(itemPosition: Int): View {
        val headerPosition = getHeaderPositionForItem(itemPosition)
        adapter.bindViewHolder(header as T, headerPosition)
        return header.itemView
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
            if (isHeader(position)) return position
            position--
        }
        return 0
    }

    private fun isHeader(position: Int): Boolean = adapter.getItemViewType(position) == listener.getHeaderLayout()

}