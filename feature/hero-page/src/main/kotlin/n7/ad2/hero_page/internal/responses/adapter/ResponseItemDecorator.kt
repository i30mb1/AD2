package n7.ad2.hero_page.internal.responses.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

class ResponseItemDecorator : RecyclerView.ItemDecoration() {

    private val offsetHorizontal = 4.dpToPx
    private val offsetVertical = 8.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val type = parent.adapter?.getItemViewType(position) ?: return
//        if (type == n7.ad2.ui.R.layout.item_header) return

        with(outRect) {
            top = offsetHorizontal
            left = offsetVertical
            right = offsetVertical
            bottom = offsetHorizontal
        }


    }

}