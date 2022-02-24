package n7.ad2.hero_page.internal.responses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.ItemResponseBodyBinding
import n7.ad2.hero_page.databinding.ItemResponseTitleBinding
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseBody
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseTitle
import n7.ad2.ui.StickyHeaderDecorator

class ResponsesAdapter(
    private val showDialogResponse: (VOResponseBody) -> Unit,
) : ListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = when (viewType) {
//        R.layout.item_response_body -> ResponseBodyViewHolder.from(viewGroup, audioExoPlayer, showDialogResponse, infoPopupWindow)
        R.layout.item_response_title -> ResponseHeaderViewHolder.from(viewGroup)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewGroup")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (viewHolder) {
            is ResponseBodyViewHolder -> if (item != null) viewHolder.bind(item as VOResponseBody) else viewHolder.clear()
            is ResponseHeaderViewHolder -> if (item != null) viewHolder.bind(item as VOResponseTitle) else viewHolder.clear()
            else -> throw UnsupportedOperationException("could not bind for $viewHolder")
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
        is VOResponseTitle -> R.layout.item_response_title
        is VOResponseBody -> R.layout.item_response_body
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun getHeaderLayout() = R.layout.item_response_title

    class ResponseHeaderViewHolder private constructor(
        private val binding: ItemResponseTitleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponseTitle) {
//            binding.item = item
//            binding.executePendingBindings()
        }

        fun clear() = Unit

        companion object {
            fun from(parent: ViewGroup): ResponseHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseTitleBinding.inflate(layoutInflater, parent, false)
                return ResponseHeaderViewHolder(binding)
            }
        }

    }

    class ResponseBodyViewHolder private constructor(
        private val binding: ItemResponseBodyBinding,
        private val showDialogResponse: (VOResponseBody) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(isSaved: Boolean) {
//            binding.ivIsSaved.isVisible = isSaved
//            binding.executePendingBindings()
        }

        fun bind(item: VOResponseBody) {
//            (binding.rv.layoutManager as GridLayoutManager).spanCount = clamp(item.icons.size, MIN_ICONS_IN_ROW, MAX_ICONS_IN_ROW)
//            binding.root.setOnLongClickListener {
//                showDialogResponse(item)
//                true
//            }
        }

        fun clear() = Unit

        companion object {
            private const val MAX_ICONS_IN_ROW = 3
            private const val MIN_ICONS_IN_ROW = 1
            private const val MAX_VIEWS_RESPONSE_IMAGE = 60
            private val viewPool = RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(R.layout.item_response_image, MAX_VIEWS_RESPONSE_IMAGE)
            }

            fun from(
                parent: ViewGroup,
//                audioExoPlayer: AudioExoPlayer,
                showDialogResponse: (VOResponseBody) -> Unit,
                infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
            ): ResponseBodyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseBodyBinding.inflate(layoutInflater, parent, false).also {
//                    it.infoPopupWindow = infoPopupWindow
//                    it.audioExoPlayer = audioExoPlayer
                }
                val responsesImagesAdapter = ResponsesImagesAdapter(infoPopupWindow)
                val gridLayoutManager = GridLayoutManager(parent.context, MAX_ICONS_IN_ROW).apply {
                    recycleChildrenOnDetach = true
                    initialPrefetchItemCount = 12
                }
//                binding.rv.apply {
//                    setRecycledViewPool(viewPool)
//                    setHasFixedSize(true)
//                    layoutManager = gridLayoutManager
//                    adapter = responsesImagesAdapter
//                    setItemViewCacheSize(MAX_VIEWS_RESPONSE_IMAGE)
//                }
                return ResponseBodyViewHolder(binding, showDialogResponse)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOResponse>() {
        override fun areItemsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem == newItem
        override fun getChangePayload(oldItem: VOResponse, newItem: VOResponse): Any? {
            if (oldItem is VOResponseBody && newItem is VOResponseBody) {
                if (oldItem.isSavedInMemory != newItem.isSavedInMemory) return newItem.isSavedInMemory
            }
            return super.getChangePayload(oldItem, newItem)
        }
    }

}