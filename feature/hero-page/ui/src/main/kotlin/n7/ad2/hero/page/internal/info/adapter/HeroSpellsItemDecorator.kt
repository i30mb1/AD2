package n7.ad2.hero.page.internal.info.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

class HeroSpellsItemDecorator : RecyclerView.ItemDecoration() {

    private val leftOffset = 6.dpToPx
    private val rightOffset = 6.dpToPx
    private val offsetHorizontal = 4.dpToPx
    private val offsetVertical = 4.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: return

        with(outRect) {
            top = offsetHorizontal
            bottom = offsetHorizontal
            left = if (position == 0) leftOffset else offsetVertical
            right = if (position == childCount - 1) rightOffset else offsetVertical
        }
    }
}
