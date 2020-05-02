package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemSpellBinding
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsListAdapter : ListAdapter<VOSpell, SpellsListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(private val binding: ItemSpellBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(spell: VOSpell) {
            binding.iv.setImageResource(R.drawable.spell_placeholder)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {

    override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = true
}
