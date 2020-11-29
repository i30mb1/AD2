package n7.ad2.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import n7.ad2.R
import n7.ad2.base.VOModelListener
import n7.ad2.databinding.ItemItemBinding
import n7.ad2.databinding.ItemItemHeaderBinding
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.ui.items.domain.vo.VOItemHeader

class ItemsPagedListAdapter(
    fragment: ItemsFragment,
) : PagedListAdapter<VOItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        const val SPAN_SIZE_ITEM = 1
        const val SPAN_SIZE_ITEM_HEADER = 4
    }

    private val listener = object : VOModelListener<ItemItemBinding> {
        override fun onClickListener(model: ItemItemBinding) {
            fragment.startItemInfoFragment(model.model!!, model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_item -> ItemViewHolder.from(parent, listener)
        R.layout.item_item_header -> HeaderViewHolder.from(parent)
        else -> throw NotImplementedError()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemViewHolder -> if (item != null) holder.bindTo(item as VOItemBody) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bindTo(item as VOItemHeader)
            else -> throw NotImplementedError()
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
            is VOItem -> R.layout.item_item
            is VOItemHeader -> R.layout.item_item_header
            else -> throw NotImplementedError()
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
        private val binding: ItemItemBinding,
        private val listener: VOModelListener<ItemItemBinding>,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(model: VOItemBody) = binding.let {
            it.model = model
            it.listener = listener
            it.binding = binding
            it.executePendingBindings()
        }

        fun clear() = binding.apply {
            iv.clear()
            tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup, listener: VOModelListener<ItemItemBinding>): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemItemBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding, listener)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem) = when (oldItem) {
            is VOItemBody -> newItem is VOItemBody
            is VOItemHeader -> newItem is VOItemHeader
        }

        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem) = oldItem == newItem

    }

}