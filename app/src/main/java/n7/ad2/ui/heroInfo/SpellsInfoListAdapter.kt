package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemSpellInfoBinding
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsInfoListAdapter : ListAdapter<VOSpell, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderSpellInfoPlain.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderSpellInfoPlain).bind(getItem(position))
    }


    class ViewHolderSpellInfoPlain private constructor(private val binding: ItemSpellInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOSpell) {

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderSpellInfoPlain {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellInfoBinding.inflate(layoutInflater, parent, false)
                return ViewHolderSpellInfoPlain(binding)
            }
        }

    }

}
