package n7.ad2.itempage.internal.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

internal class ItemInfoItemDecorator : RecyclerView.ItemDecoration() {

    var statusBarsInsets = 0
    var navigationBarsInsets = 0

    private val offsetHorizontal = 4.dpToPx
    private val offsetVertical = 8.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: return

        with(outRect) {
            left = offsetVertical
            right = offsetVertical
            top = if (position == 0) offsetHorizontal + statusBarsInsets else offsetHorizontal
            bottom = if (position == childCount - 1) offsetHorizontal + navigationBarsInsets else offsetHorizontal
        }

    }

}