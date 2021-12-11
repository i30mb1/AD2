package n7.ad2.item_page.internal.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ktx.dpToPx

class RecipeItemDecorator : RecyclerView.ItemDecoration() {

    private val offsetHorizontal = 2.dpToPx
    private val offsetVertical = 2.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        with(outRect) {
            left = offsetVertical
            right = offsetVertical
            top = offsetHorizontal
            bottom = offsetHorizontal
        }

    }
}