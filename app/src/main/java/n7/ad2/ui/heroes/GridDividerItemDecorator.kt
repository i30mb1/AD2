package n7.ad2.ui.heroes

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.utils.extension.toPx

class GridDividerItemDecorator : RecyclerView.ItemDecoration() {

    var offsetTop = 0
    var offsetBottom = 0
    private val offset = 3.toPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanCount: Int = layoutManager.spanCount
        val column: Int = position % spanCount
        val childCount = parent.adapter?.itemCount ?: return
        val type = parent.adapter?.getItemViewType(position) ?: return

        val leftOffset = if (column == 0) offset else offset / 2
        val rightOffset = if (column == spanCount - 1) offset else offset / 2
        with(outRect) {
            bottom = if (position == childCount - 1) offsetBottom else offset
            top = if (position in (0 until spanCount)) offsetTop else offset
            left = offset
            right = offset
        }

    }

}