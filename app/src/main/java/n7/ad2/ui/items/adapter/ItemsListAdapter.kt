package n7.ad2.ui.items.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.ui.items.domain.vo.VOItemHeader
import n7.ad2.utils.ImageLoader

class ItemsListAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ImageLoader,
    onItemClick: (hero: VOItemBody) -> Unit,
) : ListAdapter<VOItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val itemClickListener = { model: VOItemBody ->
        onItemClick(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_item_header -> ItemHeaderViewHolder.from(layoutInflater, parent)
        R.layout.item_item_body -> ItemBodyViewHolder.from(layoutInflater, parent, imageLoader, itemClickListener)
        else -> super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemHeaderViewHolder -> if (item != null) holder.bind(item as VOItemHeader) else holder.clear()
            is ItemBodyViewHolder -> if (item != null) holder.bind(item as VOItemBody) else holder.clear()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is ItemHeaderViewHolder -> holder.clear()
            is ItemBodyViewHolder -> holder.clear()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOItemBody -> R.layout.item_item_body
            is VOItemHeader -> R.layout.item_item_header
            else -> super.getItemViewType(position)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem == newItem
    }

}