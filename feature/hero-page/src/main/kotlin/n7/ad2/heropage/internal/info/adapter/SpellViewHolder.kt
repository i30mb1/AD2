package n7.ad2.heropage.internal.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.load
import n7.ad2.feature.heropage.databinding.ItemSpellBinding
import n7.ad2.heropage.internal.info.domain.vo.VOSpell

class SpellViewHolder(
    private val binding: ItemSpellBinding,
    private val onSpellClickListener: (spell: VOSpell.Simple) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOSpell.Simple) {
        binding.iv.load(item.urlSpellImage, n7.ad2.core.ui.R.drawable.square_placeholder, n7.ad2.core.ui.R.drawable.square_error_placeholder)
        binding.root.setOnClickListener { onSpellClickListener(item) }
        bind(item.isSelected)
    }

    fun bind(isSelected: Boolean) {
        binding.iv.isSelected = isSelected
        binding.root.isSelected = isSelected
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onSpellClickListener: (spell: VOSpell.Simple) -> Unit,
        ): SpellViewHolder {
            val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
            return SpellViewHolder(binding, onSpellClickListener)
        }
    }

}