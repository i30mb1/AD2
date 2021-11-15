package n7.ad2.ui.heroes.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.utils.extension.toPx

class HeroesItemDecorator : RecyclerView.ItemDecoration() {

    var statusBarsInsets = 0
    var navigationBarsInsets = 0
    var percent = 0f

    private val offset = 3.toPx
    private val offsetBottom = 6.toPx
    private val offsetTop = 6.toPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanCount: Int = layoutManager.spanCount
        val column: Int = position % spanCount
        val childCount = parent.adapter?.itemCount ?: return
        val itemsInEnd = childCount % spanCount
        val type = parent.adapter?.getItemViewType(position) ?: return

        with(outRect) {
            bottom = if (position in (childCount - itemsInEnd..childCount)) offsetBottom + (navigationBarsInsets * percent).toInt() else offset
            top = if (position in (0 until spanCount)) offsetTop + (statusBarsInsets * percent).toInt() else offset
            left = offset
            right = offset
        }

    }

}