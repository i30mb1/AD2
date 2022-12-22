package n7.ad2.heropage.internal.responses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.heropage.R
import n7.ad2.heropage.internal.responses.domain.vo.VOResponse
import n7.ad2.ui.StickyHeaderDecorator
import n7.ad2.ui.adapter.HeaderViewHolder
import java.lang.ref.WeakReference

class ResponsesAdapter(
    private val layoutInflater: LayoutInflater,
    private val showDialogResponse: (VOResponse.Body) -> Unit,
    private val playSound: (VOResponse.Body) -> Unit,
    private val showPopup: () -> Unit,
) : ListAdapter<VOResponse, RecyclerView.ViewHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    private val activeViewHolders = ArrayList<WeakReference<RecyclerView.ViewHolder>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_response_body -> ResponseBodyViewHolder.from(layoutInflater, viewGroup, showDialogResponse, playSound, showPopup)
        n7.ad2.ui.R.layout.item_header -> HeaderViewHolder.from(layoutInflater, viewGroup)
        else -> error("could not find ViewHolder for $viewGroup")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        activeViewHolders.add(WeakReference(viewHolder))
        val item = getItem(position)
        when (viewHolder) {
            is ResponseBodyViewHolder -> if (item != null) viewHolder.bind(item as VOResponse.Body) else viewHolder.clear()
            is HeaderViewHolder -> if (item != null) viewHolder.bind((item as VOResponse.Title).data) else viewHolder.clear()
            else -> error("could not bind for $viewHolder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) = when {
        payloads.isNullOrEmpty() -> super.onBindViewHolder(holder, position, payloads)
        holder is ResponseBodyViewHolder -> holder.bind(payloads.last() as Boolean)
        else -> error("could not bind viewHolder $holder")
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        for (i in 0 until activeViewHolders.size - 1) {
            val currentViewHolder = activeViewHolders[i].get()
            if (currentViewHolder == null || currentViewHolder == holder) activeViewHolders.removeAt(i)
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOResponse.Title -> n7.ad2.ui.R.layout.item_header
        is VOResponse.Body -> R.layout.item_response_body
        else -> error("could not get type for $position")
    }

    override fun getHeaderLayout() = n7.ad2.ui.R.layout.item_header

    fun onUploadProgress(downloadedBytes: Int, totalBytes: Int, downloadID: Long) {
        activeViewHolders.forEach {
            (it.get() as? ResponseBodyViewHolder)?.updateUploadProgress(downloadedBytes, totalBytes, downloadID)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOResponse>() {
        override fun areItemsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOResponse, newItem: VOResponse) = oldItem == newItem
        override fun getChangePayload(oldItem: VOResponse, newItem: VOResponse): Any? = when {
            oldItem is VOResponse.Body && newItem is VOResponse.Body -> {
                if (oldItem.isSavedInMemory != newItem.isSavedInMemory) newItem.isSavedInMemory else null
            }
            else -> super.getChangePayload(oldItem, newItem)
        }
    }

}