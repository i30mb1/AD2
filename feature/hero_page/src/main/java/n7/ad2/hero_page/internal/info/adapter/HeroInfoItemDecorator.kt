package n7.ad2.hero_page.internal.info.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

class HeroInfoItemDecorator : RecyclerView.ItemDecoration() {

    private val topOffset = 16.dpToPx
    private val botOffset = 16.dpToPx
    private val offsetHorizontal = 8.dpToPx
    private val offsetVertical = 8.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: return
        val type = parent.adapter?.getItemViewType(position) ?: return

        with(outRect) {
            top = offsetHorizontal
            left = offsetVertical
            right = offsetVertical
            if (position == 0) top = topOffset
            if (position == childCount - 1) bottom = botOffset
        }

    }

}