package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onSpellClickListener: (spell: VOSpell) -> Unit,
) : ListAdapter<VOSpell, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_spell -> SpellViewHolder.from(layoutInflater, parent, onSpellClickListener)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOSpell -> R.layout.item_spell
        else -> throw UnsupportedOperationException("could not get type for $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SpellViewHolder -> if (item != null) holder.bind(item) else holder.clear()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNullOrEmpty() -> super.onBindViewHolder(holder, position, payloads)
            holder is SpellViewHolder -> holder.bind((payloads.last() as VOSpell).isSelected)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {
        override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem == newItem
        override fun getChangePayload(oldItem: VOSpell, newItem: VOSpell): Any? {
            if (oldItem.isSelected != newItem.isSelected) return newItem
            return super.getChangePayload(oldItem, newItem)
        }
    }

}


