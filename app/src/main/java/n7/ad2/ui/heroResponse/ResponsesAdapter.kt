package n7.ad2.ui.heroResponse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemResponseBodyBinding
import n7.ad2.databinding.ItemResponseTitleBinding
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseTitle
import n7.ad2.utils.StickyHeaderDecorator.StickyHeaderInterface

class ResponsesAdapter(
    private val audioExoPlayer: AudioExoPlayer,
    private val infoPopupWindow: InfoPopupWindow,
    private val showDialogResponse: (VOResponseBody) -> Unit,
) : PagedListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_response_body -> BodyViewHolder.from(viewGroup, audioExoPlayer, showDialogResponse, infoPopupWindow)
        R.layout.item_response_title -> HeaderViewHolder.from(viewGroup)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewGroup")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (viewHolder) {
            is BodyViewHolder -> if (item != null) viewHolder.bind(item as VOResponseBody) else viewHolder.clear()
            is HeaderViewHolder -> if (item != null) viewHolder.bind(item as VOResponseTitle) else viewHolder.clear()
            else -> throw UnsupportedOperationException("could not bind for $viewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOResponseTitle -> R.layout.item_response_title
        is VOResponseBody -> R.layout.item_response_body
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun getHeaderLayout() = R.layout.item_response_title

    class HeaderViewHolder private constructor(
        private val binding: ItemResponseTitleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponseTitle) {
            binding.item = item
            binding.executePendingBindings()
        }

        fun clear() = Unit

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseTitleBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }

    }

    class BodyViewHolder private constructor(
        private val binding: ItemResponseBodyBinding,
        private val showDialogResponse: (VOResponseBody) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponseBody) {
            (binding.rv.layoutManager as GridLayoutManager).spanCount = clamp(item.icons.size, MIN_ICONS_IN_ROW, MAX_ICONS_IN_ROW)
            binding.root.setOnLongClickListener {
                showDialogResponse(item)
                true
            }
            binding.item = item
            binding.executePendingBindings()
        }

        fun clear() = Unit

        companion object {
            private const val MAX_ICONS_IN_ROW = 3
            private const val MIN_ICONS_IN_ROW = 1
            private const val MAX_VIEWS_RESPONSE_IMAGE = 60
            private val viewPool = RecyclerView.RecycledViewPool().apply { setMaxRecycledViews(R.layout.item_response_image, MAX_VIEWS_RESPONSE_IMAGE) }

            fun from(
                parent: ViewGroup,
                audioExoPlayer: AudioExoPlayer,
                showDialogResponse: (VOResponseBody) -> Unit,
                infoPopupWindow: InfoPopupWindow,
            ): BodyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseBodyBinding.inflate(layoutInflater, parent, false).also {
                    it.infoPopupWindow = infoPopupWindow
                    it.audioExoPlayer = audioExoPlayer
                }
                val responsesImagesAdapter = ResponsesImagesAdapter(infoPopupWindow)
                val gridLayoutManager = GridLayoutManager(parent.context, MAX_ICONS_IN_ROW).apply { recycleChildrenOnDetach = true }
                binding.rv.apply {
                    setRecycledViewPool(viewPool)
                    setHasFixedSize(true)
                    layoutManager = gridLayoutManager
                    adapter = responsesImagesAdapter
//                    setItemViewCacheSize(MAX_VIEWS_RESPONSE_IMAGE)
                }
                return BodyViewHolder(binding, showDialogResponse)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOResponse>() {
        override fun areItemsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem == newItem
    }

}