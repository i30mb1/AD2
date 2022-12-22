package n7.ad2.heropage.internal.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.heropage.R
import n7.ad2.heropage.internal.info.domain.vo.VOSpell

class SpellsListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onSpellClickListener: (spell: VOSpell.Simple) -> Unit,
    private val onTalentClickListener: (spell: VOSpell.Talent) -> Unit,
) : ListAdapter<VOSpell, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_spell -> SpellViewHolder.from(layoutInflater, parent, onSpellClickListener)
        R.layout.item_talent -> TalentViewHolder.from(layoutInflater, parent, onTalentClickListener)
        else -> error("could not find ViewHolder for $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOSpell.Simple -> R.layout.item_spell
        is VOSpell.Talent -> R.layout.item_talent
        else -> error("could not get type for $position")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SpellViewHolder -> if (item != null) holder.bind(item as VOSpell.Simple)
            is TalentViewHolder -> if (item != null) holder.bind(item as VOSpell.Talent)
            else -> error("nothing to bind")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isEmpty() -> super.onBindViewHolder(holder, position, payloads)
            holder is SpellViewHolder -> holder.bind((payloads.last() as VOSpell.Simple).isSelected)
            holder is TalentViewHolder -> holder.bind((payloads.last() as VOSpell.Talent).isSelected)
            else -> error("nothing to bind")
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {
        override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem == newItem
        override fun getChangePayload(oldItem: VOSpell, newItem: VOSpell): Any? {
            if (oldItem is VOSpell.Talent && newItem is VOSpell.Talent && oldItem.isSelected != newItem.isSelected) return newItem
            if (oldItem is VOSpell.Simple && newItem is VOSpell.Simple && oldItem.isSelected != newItem.isSelected) return newItem
            return super.getChangePayload(oldItem, newItem)
        }
    }

}


