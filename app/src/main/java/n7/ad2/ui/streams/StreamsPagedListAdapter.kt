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
import n7.ad2.utils.ImageLoader

class StreamsPagedListAdapter(
    private val imageLoader: ImageLoader,
    private val onStreamClick: (vOSimpleStream: VOSimpleStream) -> Unit,
) : PagingDataAdapter<VOStream, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_list_stream -> StreamViewHolder.from(parent, imageLoader)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOSimpleStream -> R.layout.item_list_stream
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StreamViewHolder -> holder.bindTo(item as VOSimpleStream, onStreamClick)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is StreamViewHolder -> holder.unbind()
        }
    }

    class StreamViewHolder(
        private val binding: ItemListStreamBinding,
        private val imageLoader: ImageLoader,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            vOSimpleStream: VOSimpleStream,
            onStreamClick: (vOSimpleStream: VOSimpleStream) -> Unit,
        ) = binding.apply {
            binding.tvTitle.text = vOSimpleStream.title
            binding.root.setOnClickListener { onStreamClick(vOSimpleStream) }
            imageLoader.load(binding.ivStreamImage, vOSimpleStream.imageUrl, R.drawable.streams_placeholder)
        }

        fun unbind() {
            binding.root.setOnClickListener(null)
            imageLoader.clear(binding.ivStreamImage)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                imageLoader: ImageLoader,
            ): StreamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListStreamBinding.inflate(layoutInflater, parent, false)
                return StreamViewHolder(binding, imageLoader)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOStream>() {
        override fun areItemsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOStream, newItem: VOStream) = oldItem == newItem
    }
}