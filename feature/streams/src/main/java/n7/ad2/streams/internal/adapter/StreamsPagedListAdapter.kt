package n7.ad2.streams.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.streams.R
import n7.ad2.streams.internal.domain.vo.VOStream

internal class StreamsPagedListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onStreamClick: (voStream: VOStream) -> Unit,
) : PagingDataAdapter<VOStream, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_list_stream -> StreamSimpleViewHolder.from(layoutInflater, parent)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOStream.Simple -> R.layout.item_list_stream
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StreamSimpleViewHolder -> holder.bind(item as VOStream.Simple, onStreamClick)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is StreamSimpleViewHolder -> holder.unbind()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOStream>() {
        override fun areItemsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem == newItem
    }
}