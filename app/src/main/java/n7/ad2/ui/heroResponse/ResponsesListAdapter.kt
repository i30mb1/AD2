package n7.ad2.ui.heroResponse

import android.os.Trace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.math.MathUtils.clamp
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.databinding.ItemResponseBodyBinding
import n7.ad2.databinding.ItemResponseHeaderBinding
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import n7.ad2.utils.StickyHeaderDecorator.StickyHeaderInterface

class ResponsesListAdapter(
    private val audioExoPlayer: AudioExoPlayer,
    private val showDialogResponse: (VOResponseBody) -> Unit
) : PagedListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_response_body -> BodyViewHolder.from(viewGroup, audioExoPlayer, showDialogResponse)
            else -> HeaderViewHolder.from(viewGroup)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (viewHolder) {
            is BodyViewHolder -> if (item != null) viewHolder.bind(item as VOResponseBody) else viewHolder.clear()
            is HeaderViewHolder -> if (item != null) viewHolder.bind(item as VOResponseHeader) else viewHolder.clear()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOResponseHeader -> R.layout.item_response_header
            else -> R.layout.item_response_body
        }
    }

    override fun getHeaderLayout(headerPosition: Int) = R.layout.item_response_header

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val item = getItem(headerPosition) ?: return
        header?.findViewById<TextView>(R.id.tv_item_response)?.text = (item as VOResponseHeader).title
    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition < 0 || itemPosition >= itemCount) return false
        return when (getItem(itemPosition)) {
            is VOResponseHeader -> true
            else -> false
        }
    }

    class HeaderViewHolder private constructor(
        private val binding: ItemResponseHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponseHeader) {
            binding.item = item
            binding.executePendingBindings()
        }

        fun clear() {

        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }

    }

    class BodyViewHolder(
        private val binding: ItemResponseBodyBinding,
        private val audioExoPlayer: AudioExoPlayer,
        private val showDialogResponse: (VOResponseBody) -> Unit,
        private val responsesImagesAdapter: ResponsesImagesAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponseBody) {
            Trace.beginSection("bind")
            binding.audioExoPlayer = audioExoPlayer
            binding.rv.adapter = responsesImagesAdapter
            (binding.rv.layoutManager as GridLayoutManager).spanCount = clamp(item.icons.size, MIN_ICONS_IN_ROW, MAX_ICONS_IN_ROW)
            binding.root.setOnLongClickListener {
                showDialogResponse(item)
                true
            }
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            Trace.endSection()
        }

        fun clear() {
        }

        companion object {

            private const val MAX_ICONS_IN_ROW = 3
            private const val MIN_ICONS_IN_ROW = 1
            private const val MAX_VIEWS_RESPONSE_IMAGE = 60
            private val viewPool = RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(R.layout.item_response_image, MAX_VIEWS_RESPONSE_IMAGE)
            }

            fun from(
                parent: ViewGroup,
                audioExoPlayer: AudioExoPlayer,
                showDialogResponse: (VOResponseBody) -> Unit
            ): BodyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResponseBodyBinding.inflate(layoutInflater, parent, false)
                binding.rv.setRecycledViewPool(viewPool)
//                binding.rv.setItemViewCacheSize(MAX_VIEWS_RESPONSE_IMAGE)
                binding.rv.setHasFixedSize(true)
                (binding.rv.layoutManager as GridLayoutManager).recycleChildrenOnDetach = true
                val responsesImagesAdapter = ResponsesImagesAdapter()
                return BodyViewHolder(binding, audioExoPlayer, showDialogResponse, responsesImagesAdapter)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOResponse>() {

        override fun areItemsTheSame(oldItem: VOResponse, newItem: VOResponse): Boolean {
            return when (oldItem) {
                is VOResponseBody -> newItem is VOResponseBody
                is VOResponseHeader -> newItem is VOResponseHeader
            }
        }

        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse): Boolean {
            return when (oldItem) {
                is VOResponseHeader -> if (newItem is VOResponseBody) return false else {
                    oldItem == (newItem as VOResponseHeader)
                }
                is VOResponseBody -> if(newItem is VOResponseHeader) return false else {
                    oldItem == (newItem as VOResponseBody)
                }
            }
        }

    }

}