package n7.ad2.hero_page.internal.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.databinding.ItemSpellBinding
import n7.ad2.hero_page.internal.info.domain.vo.VOSpell

class SpellViewHolder(
    private val binding: ItemSpellBinding,
    private val onSpellClickListener: (spell: VOSpell) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOSpell) {
//        binding.iv.load(item.urlSpellImage, R.drawable.spell_placeholder)
        binding.root.setOnClickListener { onSpellClickListener(item) }
        bind(item.isSelected)
    }

    fun bind(isSelected: Boolean) {
        binding.iv.isSelected = isSelected
        binding.root.isSelected = isSelected
    }

    fun clear() {
//        binding.iv.clear()
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onSpellClickListener: (spell: VOSpell) -> Unit,
        ): SpellViewHolder {
            val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
            return SpellViewHolder(binding, onSpellClickListener)
        }
    }

}