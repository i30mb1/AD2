package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemSpellBinding
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsListAdapter : ListAdapter<VOSpell, SpellsListAdapter.ViewHolder>(DiffCallback()) {

    private val listener: View.OnClickListener = View.OnClickListener {
        it.isSelected = !it.isSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(
            private val binding: ItemSpellBinding,
            private val listener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VOSpell) = binding.let {
            it.model = model
            it.listener = listener
            it.executePendingBindings()
        }

        companion object {
            fun from(
                    parent: ViewGroup,
                    listener: View.OnClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, listener)
            }
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<VOSpell>() {

    override fun areItemsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: VOSpell, newItem: VOSpell): Boolean = true
}
