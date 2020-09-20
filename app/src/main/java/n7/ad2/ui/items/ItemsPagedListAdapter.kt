package n7.ad2.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.clear
import n7.ad2.base.VOModelListener
import n7.ad2.databinding.ItemItemBinding
import n7.ad2.ui.items.domain.vo.VOItem

class ItemsPagedListAdapter constructor(fragment: ItemsFragment) : PagedListAdapter<VOItem, ItemsPagedListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = object : VOModelListener<ItemItemBinding> {
        override fun onClickListener(model: ItemItemBinding) {
            fragment.startItemInfoFragment(model.model!!, model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bindTo(item) else holder.clear()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    class ViewHolder(
        private val binding: ItemItemBinding,
        private val listener: VOModelListener<ItemItemBinding>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: VOItem) = binding.let {
            it.model = item
            it.listener = listener
            it.root.setOnClickListener {
                listener.onClickListener(binding)
            }
            it.executePendingBindings()
        }

        fun clear() = binding.apply {
            iv.clear()
            tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup, listener: VOModelListener<ItemItemBinding>): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, listener)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItem>() {
        override fun areItemsTheSame(oldItem: VOItem, newItem: VOItem): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: VOItem, newItem: VOItem): Boolean = true

    }

}