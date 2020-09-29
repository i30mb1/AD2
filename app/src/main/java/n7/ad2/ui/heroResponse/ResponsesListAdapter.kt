package n7.ad2.ui.heroResponse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.math.MathUtils.clamp
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.databinding.ItemResponseBodyBinding
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import n7.ad2.utils.StickyHeaderDecorator.StickyHeaderInterface

class ResponsesListAdapter(
        private val audioExoPlayer: AudioExoPlayer,
        private val showDialogResponse: (VOResponseBody) -> Unit
) : PagedListAdapter<VOResponse, ResponsesListAdapter.ViewHolder>(DiffCallback()), StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(viewGroup, viewType, audioExoPlayer, showDialogResponse)

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) viewHolder.bind(item) else viewHolder.clear()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOResponseHeader -> R.layout.item_response_header
            else -> R.layout.item_response_body
        }
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int) = R.layout.item_response_header

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val item = getItem(headerPosition) ?: return
        header?.findViewById<TextView>(R.id.tv_item_response)?.text = (item as VOResponseHeader).title
    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition < 0 || itemPosition >= itemCount) return false
        return when(getItem(itemPosition)) {
            is VOResponseHeader -> true
            else -> false
        }
    }

    class ViewHolder(
            private val binding: ViewDataBinding,
            private val audioExoPlayer: AudioExoPlayer,
            private val showDialogResponse: (VOResponseBody) -> Unit,
            private val responsesImagesAdapter: ResponsesImagesAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponse) {
            bindSpecificVO(binding, item)
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }

        private fun bindSpecificVO(binding: ViewDataBinding, item: VOResponse) {
            when (binding) {
                is ItemResponseBodyBinding -> {
                    binding.audioExoPlayer = audioExoPlayer
                    binding.rv.adapter = responsesImagesAdapter
                    (binding.rv.layoutManager as GridLayoutManager).spanCount = clamp((item as VOResponseBody).icons.size, MIN_ICONS_IN_ROW, MAX_ICONS_IN_ROW)
                    binding.root.setOnLongClickListener {
                        showDialogResponse(item)
                        true
                    }
                }
            }

        }

        fun clear() {

        }

        companion object {

            const val MAX_ICONS_IN_ROW = 3
            const val MIN_ICONS_IN_ROW = 1

            fun from(
                parent: ViewGroup,
                viewType: Int,
                audioExoPlayer: AudioExoPlayer,
                showDialogResponse: (VOResponseBody) -> Unit
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
                val responsesImagesAdapter = ResponsesImagesAdapter()
                return ViewHolder(binding, audioExoPlayer, showDialogResponse, responsesImagesAdapter)
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
                is VOResponseHeader -> if (newItem is VOResponseHeader) return true else false
                is VOResponseBody -> if(newItem is VOResponseHeader) return false else {
                    oldItem.title == (newItem as VOResponseBody).title && oldItem.savedInMemory == newItem.savedInMemory
                }
            }
        }

    }

}