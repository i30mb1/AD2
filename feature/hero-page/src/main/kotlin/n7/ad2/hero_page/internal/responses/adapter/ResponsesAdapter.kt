package n7.ad2.hero_page.internal.responses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.R
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import n7.ad2.ui.StickyHeaderDecorator
import n7.ad2.ui.adapter.HeaderViewHolder

class ResponsesAdapter(
    private val layoutInflater: LayoutInflater,
    private val showDialogResponse: (VOResponse.Body) -> Unit,
    private val playSound: (VOResponse.Body) -> Unit,
    private val showPopup: () -> Unit,
) : ListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_response_body -> ResponseBodyViewHolder.from(layoutInflater, viewGroup, showDialogResponse, playSound, showPopup)
        n7.ad2.ui.R.layout.item_header -> HeaderViewHolder.from(layoutInflater, viewGroup)
        else -> error("could not find ViewHolder for $viewGroup")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (viewHolder) {
            is ResponseBodyViewHolder -> if (item != null) viewHolder.bind(item as VOResponse.Body) else viewHolder.clear()
            is HeaderViewHolder -> if (item != null) viewHolder.bind((item as VOResponse.Title).data) else viewHolder.clear()
            else -> error("could not bind for $viewHolder")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            (holder as ResponseBodyViewHolder).bind(payloads.last() as Boolean)
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOResponse.Title -> n7.ad2.ui.R.layout.item_header
        is VOResponse.Body -> R.layout.item_response_body
        else -> error("could not get type for $position")
    }

    override fun getHeaderLayout() = n7.ad2.ui.R.layout.item_header

    private class DiffCallback : DiffUtil.ItemCallback<VOResponse>() {
        override fun areItemsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem == newItem
        override fun getChangePayload(oldItem: VOResponse, newItem: VOResponse): Any? {
            if (oldItem is VOResponse.Body && newItem is VOResponse.Body) {
                if (oldItem.isSavedInMemory != newItem.isSavedInMemory) return newItem.isSavedInMemory
            }
            return super.getChangePayload(oldItem, newItem)
        }
    }

}