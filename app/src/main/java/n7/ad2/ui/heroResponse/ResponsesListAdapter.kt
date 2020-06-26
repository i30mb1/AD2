package n7.ad2.ui.heroResponse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.ui.heroResponse.domain.vo.VOResponseHeader
import n7.ad2.utils.StickyHeaderDecorator.StickyHeaderInterface

// https://youtu.be/xF1x-Pm6IPw
class ResponsesListAdapter constructor(
) : PagedListAdapter<VOResponse, ResponsesListAdapter.ViewHolder>(DiffCallback()), StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(viewGroup, viewType)

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
        var itemPosition = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition
                break
            }
            itemPosition -= 1
        } while (itemPosition >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int) = R.layout.item_response_header

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val item = getItem(headerPosition)
        if (item == null) return
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
            val binding: ViewDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponse) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }


        fun clear() {

        }

        companion object {
            fun from(parent: ViewGroup,
                     viewType: Int): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
                return ViewHolder(binding)
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

        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse): Boolean = false

    }

}