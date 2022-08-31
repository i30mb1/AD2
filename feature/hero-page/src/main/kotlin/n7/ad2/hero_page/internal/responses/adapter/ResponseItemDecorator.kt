package n7.ad2.hero_page.internal.responses.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel
import n7.ad2.ktx.dpToPx

class ResponseItemDecorator : RecyclerView.ItemDecoration() {

    private val offsetHorizontal = 4.dpToPx
    private val offsetVertical = 8.dpToPx
    private val shapeForHeader = ShapeAppearanceModel.builder()
        .setAllCornerSizes(0f)
        .build()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val type = parent.adapter?.getItemViewType(position) ?: return
        if (type == n7.ad2.ui.R.layout.item_header) {
            if (view is MaterialCardView) {
                view.shapeAppearanceModel = shapeForHeader
                // val drawable = MaterialShapeDrawable(shapeForHeader)
            }
        }

        with(outRect) {
            top = offsetHorizontal
            left = offsetVertical
            right = offsetVertical
            bottom = offsetHorizontal
        }


    }

}