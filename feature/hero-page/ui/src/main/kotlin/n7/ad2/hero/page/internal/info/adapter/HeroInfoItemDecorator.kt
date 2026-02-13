package n7.ad2.hero.page.internal.info.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

class HeroInfoItemDecorator : RecyclerView.ItemDecoration() {

    var navigationBarsInsets = 0
    private val topOffset = 6.dpToPx
    private val botOffset = 0.dpToPx
    private val offsetHorizontal = 4.dpToPx
    private val offsetVertical = 8.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: return

        with(outRect) {
            top = when (position) {
                0 -> topOffset
                else -> offsetHorizontal
            }
            left = offsetVertical
            right = offsetVertical
            bottom = when (position) {
                childCount - 1 -> botOffset + navigationBarsInsets
                else -> offsetHorizontal
            }
        }
    }
}
