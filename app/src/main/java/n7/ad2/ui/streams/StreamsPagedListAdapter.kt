package n7.ad2.ui.streams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemListStreamBinding
import n7.ad2.ui.streams.domain.vo.VOSimpleStream
import n7.ad2.ui.streams.domain.vo.VOStream

class StreamsPagedListAdapter : PagingDataAdapter<VOStream, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_list_stream -> StreamViewHolder.from(parent)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOSimpleStream -> R.layout.item_list_stream
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StreamViewHolder -> holder.bindTo(item as VOSimpleStream)
        }
    }

    class StreamViewHolder(
        private val binding: ItemListStreamBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(stream: VOSimpleStream) = binding.apply {
            item = stream
            executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
            ): StreamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListStreamBinding.inflate(layoutInflater, parent, false)
                return StreamViewHolder(binding)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOStream>() {
        override fun areItemsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem == newItem
    }
}