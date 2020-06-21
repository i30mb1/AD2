package n7.ad2.ui.heroInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.DialogResponseBinding
import n7.ad2.databinding.ItemResponseBinding
import n7.ad2.databinding.ItemResponseHeaderBinding
import n7.ad2.heroes.full.HeaderModel
import n7.ad2.heroes.full.Response
import n7.ad2.heroes.full.ResponseModel
import n7.ad2.ui.heroInfo.domain.vo.VOResponse
import n7.ad2.ui.heroInfo.domain.vo.VOResponseBody
import n7.ad2.ui.heroInfo.domain.vo.VOResponseHeader
import n7.ad2.utils.StickyHeaderDecorator.StickyHeaderInterface

// https://youtu.be/xF1x-Pm6IPw
class ResponsesListAdapter constructor(
        private val viewModel: HeroInfoViewModel
) : PagedListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderInterface {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ViewHolder.from(viewGroup, viewType)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Response.TYPE_HEADER -> (viewHolder as HeaderViewHolder).bindTo(getItem(position) as HeaderModel?)
            Response.TYPE_RESPONSE -> (viewHolder as ResponseViewHolder).bindTo(getItem(position) as ResponseModel?)
            else -> (viewHolder as HeaderViewHolder).bindTo(getItem(position) as HeaderModel?)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is VOResponseHeader -> R.layout.item_response_header
            is VOResponseBody -> R.layout.item_response_body
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

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.item_response_header
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val item = getItem(headerPosition)
        if (item != null && item.type == Response.TYPE_RESPONSE) {
            return
        }
        val model = getItem(headerPosition) as HeaderModel?
        if (model != null) {
            (header!!.findViewById<View>(R.id.tv_item_response) as TextView).text = model.title
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition < 0 || itemPosition >= itemCount) return false
        val model = getItem(itemPosition)
        return if (model != null) {
            model.type == Response.TYPE_HEADER
        } else false
    }

    fun showDialog(context: Context?, model: ResponseModel?): Boolean {
        val builder = AlertDialog.Builder(context!!)
        val binding: DialogResponseBinding = DataBindingUtil.inflate(inflater!!, R.layout.dialog_response, null, false)
        binding.model = model
        binding.viewModel = viewModel
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyMaterialAlertDialogTheme
        binding.dialog = dialog
        dialog.show()
        return true
    }

    internal inner class HeaderViewHolder(var binding: ItemResponseHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(model: HeaderModel?) {
            binding.model = model
            binding.executePendingBindings()
        }

        fun clear() {}

    }

    class ViewHolder(
            val binding: ViewDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOResponse) {
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