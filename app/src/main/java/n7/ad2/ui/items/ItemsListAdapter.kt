package n7.ad2.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import n7.ad2.R
import n7.ad2.databinding.ItemItemBodyBinding
import n7.ad2.databinding.ItemItemHeaderBinding
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.ui.items.domain.vo.VOItemHeader

class ItemsListAdapter(
    fragment: ItemsFragment,
) : ListAdapter<VOItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val itemClickListener = { model: ItemItemBodyBinding -> fragment.startItemInfoFragment(model.model!!, model) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_item_header -> HeaderViewHolder.from(parent)
        R.layout.item_item_body -> ItemViewHolder.from(parent, itemClickListener)
        else -> super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemViewHolder -> if (item != null) holder.bindTo(item as VOItemBody) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bindTo(item as VOItemHeader)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is ItemViewHolder -> holder.clear()
            is HeaderViewHolder -> holder.clear()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOItemBody -> R.layout.item_item_body
            is VOItemHeader -> R.layout.item_item_header
            else -> super.getItemViewType(position)
        }
    }

    private class HeaderViewHolder private constructor(
        private val binding: ItemItemHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(model: VOItemHeader) = binding.let {
            it.model = model
            it.executePendingBindings()
        }

        fun clear() = Unit

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemItemHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }

    }

    private class ItemViewHolder private constructor(
        private val binding: ItemItemBodyBinding,
        private val itemClickListener: (ItemItemBodyBinding) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(model: VOItemBody) = binding.let {
            it.model = model
            it.itemClickListener = itemClickListener
            it.binding = binding
            it.executePendingBindings()
        }

        fun clear() = binding.apply {
            iv.clear()
            tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup, itemClickListener: (ItemItemBodyBinding) -> Unit): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemItemBodyBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding, itemClickListener)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem == newItem
    }

}