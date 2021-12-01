package n7.ad2.ui.items.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.ui.adapter.HeaderViewHolder
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.ui.items.domain.vo.VOItemHeader

class ItemsListAdapter(
    private val layoutInflater: LayoutInflater,
    onItemClick: (hero: VOItemBody) -> Unit,
) : ListAdapter<VOItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val itemClickListener = { model: VOItemBody ->
        onItemClick(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        R.layout.item_item_body -> ItemBodyViewHolder.from(layoutInflater, parent, itemClickListener)
        else -> super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemBodyViewHolder -> if (item != null) holder.bind(item as VOItemBody) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as VOItemHeader).data) else holder.clear()
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
            is VOItemBody -> R.layout.item_item_body
            is VOItemHeader -> R.layout.item_header
            else -> super.getItemViewType(position)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem == newItem
    }

}