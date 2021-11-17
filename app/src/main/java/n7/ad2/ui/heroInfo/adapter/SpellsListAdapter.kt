package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemSpellBinding
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsListAdapter(
    descriptionsListener: (List<VOHeroInfo>) -> Unit,
) : ListAdapter<VOSpell, SpellsListAdapter.ViewHolder>(DiffCallback()) {

    private val itemClickListener = { model: VOSpell ->
        descriptionsListener.invoke(model.voDescriptionList)
        deselectAll()
        model.selected = true
    }

    fun deselectAll() = currentList.forEach { item -> item.selected = false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, itemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(
        private val binding: ItemSpellBinding,
        private val itemClickListener: (VOSpell) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VOSpell) = binding.let {
            it.model = model
            it.itemClickListener = itemClickListener
            it.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: (VOSpell) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, itemClickListener)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {
        override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem == newItem
    }
}


