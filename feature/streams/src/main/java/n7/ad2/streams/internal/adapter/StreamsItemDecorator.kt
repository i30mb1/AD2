package n7.ad2.streams.internal.adapter

import ad2.n7.android.extension.toPx
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StreamsItemDecorator : RecyclerView.ItemDecoration() {

    private val topOffset = 16.toPx
    private val botOffset = 16.toPx
    private val offsetHorizontal = 8.toPx
    private val offsetVertical = 8.toPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: return

        with(outRect) {
            top = offsetHorizontal
            left = offsetVertical
            right = offsetVertical
            if (position == 0) top = topOffset
            if (position == childCount - 1) bottom = botOffset
        }

    }

}