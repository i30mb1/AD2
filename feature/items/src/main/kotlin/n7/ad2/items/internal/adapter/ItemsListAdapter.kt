package n7.ad2.items.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.items.R
import n7.ad2.items.internal.domain.vo.VOItem
import n7.ad2.ui.adapter.HeaderViewHolder

internal class ItemsListAdapter(
    private val layoutInflater: LayoutInflater,
    private val itemClickListener: (hero: VOItem.Body, view: ImageView) -> Unit,
) : ListAdapter<VOItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        R.layout.item_item_body -> ItemBodyViewHolder.from(layoutInflater, parent, itemClickListener)
        else -> super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemBodyViewHolder -> if (item != null) holder.bind(item as VOItem.Body) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as VOItem.Header).data) else holder.clear()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is HeaderViewHolder -> holder.clear()
            is ItemBodyViewHolder -> holder.clear()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOItem.Body -> R.layout.item_item_body
            is VOItem.Header -> R.layout.item_header
            else -> super.getItemViewType(position)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem == newItem
    }

}