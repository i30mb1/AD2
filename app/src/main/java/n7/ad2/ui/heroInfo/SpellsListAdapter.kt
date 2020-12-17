package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.base.VOModelListener
import n7.ad2.base.VOObjectListener
import n7.ad2.databinding.ItemSpellBinding
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsListAdapter(
    descriptionsListener: VOObjectListener<List<VODescription>>,
) : ListAdapter<VOSpell, SpellsListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = object : VOModelListener<VOSpell> {
        override fun onClickListener(model: VOSpell) {
            descriptionsListener.onClickListener(model.voDescriptionList)
            deselectAll()
            model.selected = true
        }
    }

    fun deselectAll() = currentList.forEach { item -> item.selected = false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(
        private val binding: ItemSpellBinding,
        private val listener: VOModelListener<VOSpell>,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VOSpell) = binding.let {
            it.model = model
            it.listener = listener
            it.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listener: VOModelListener<VOSpell>,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, listener)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {
        override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem == newItem
    }
}


