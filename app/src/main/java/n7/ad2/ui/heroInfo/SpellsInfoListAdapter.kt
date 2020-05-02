package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemSpellInfoBinding
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

class SpellsInfoListAdapter(fragment: HeroInfoFragment) : ListAdapter<VOSpell, RecyclerView.ViewHolder>(DiffCallback()) {

    private var listener: View.OnClickListener = View.OnClickListener {
        fragment.showPopup(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderSpellInfoPlain.from(parent, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderSpellInfoPlain).bind(getItem(position))
    }

    class ViewHolderSpellInfoPlain private constructor(
            private val binding: ItemSpellInfoBinding,
            private val listener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOSpell) {
            binding.click.setOnClickListener(listener)
            binding.click2.setOnClickListener(listener)
        }

        companion object {
            fun from(parent: ViewGroup, listener: View.OnClickListener): ViewHolderSpellInfoPlain {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSpellInfoBinding.inflate(layoutInflater, parent, false)
                return ViewHolderSpellInfoPlain(binding, listener)
            }
        }

    }

}
