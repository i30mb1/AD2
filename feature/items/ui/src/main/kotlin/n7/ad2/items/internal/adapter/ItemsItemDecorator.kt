package n7.ad2.items.internal.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.ktx.dpToPx

internal class ItemsItemDecorator : RecyclerView.ItemDecoration() {

    var statusBarsInsets = 0
    var navigationBarsInsets = 0
    var percent = 0f

    private val offset = 3.dpToPx
    private val offsetBottom = 6.dpToPx
    private val offsetTop = 6.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanCount: Int = layoutManager.spanCount
        val childCount = (parent.adapter as ItemsListAdapter).currentList.count()
        val itemsInEnd = when {
            position < childCount - spanCount -> 0
            else -> {
                val items = (parent.adapter as ItemsListAdapter).currentList.reversed().takeWhile { it is ItemUI.Body }.count() % spanCount
                if (items == 0) spanCount else items
            }
        }

        with(outRect) {
            bottom = if (position in (childCount - itemsInEnd..childCount)) offsetBottom + (navigationBarsInsets * percent).toInt() else offset
            top = if (position == 0) offsetTop + (statusBarsInsets * percent).toInt() else offset
            left = offset
            right = offset
        }

    }

}
