package n7.ad2.items.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.items.ui.R
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.ui.adapter.HeaderViewHolder

internal class ItemsListAdapter(private val layoutInflater: LayoutInflater, private val itemClickListener: (hero: ItemUI.Body, view: ImageView) -> Unit) : ListAdapter<ItemUI, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        n7.ad2.core.ui.R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        R.layout.item_item_body -> ItemBodyViewHolder.from(layoutInflater, parent, itemClickListener)
        else -> super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemBodyViewHolder -> if (item != null) holder.bind(item as ItemUI.Body) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as ItemUI.Header).data) else holder.clear()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is HeaderViewHolder -> holder.clear()
            is ItemBodyViewHolder -> holder.clear()
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ItemUI.Body -> R.layout.item_item_body
        is ItemUI.Header -> n7.ad2.core.ui.R.layout.item_header
        else -> super.getItemViewType(position)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ItemUI>() {
        override fun areItemsTheSame(oldItem: ItemUI, newItem: ItemUI) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: ItemUI, newItem: ItemUI) = oldItem == newItem
    }
}
