package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemSpellBinding
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.utils.ImageLoader

class SpellViewHolder(
    private val binding: ItemSpellBinding,
    private val imageLoader: ImageLoader,
    private val onSpellClickListener: (spell: VOSpell) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOSpell) {
        imageLoader.load(binding.iv, item.urlSpellImage, R.drawable.spell_placeholder)
        binding.root.setOnClickListener { onSpellClickListener(item) }
        bind(item.isSelected)
    }

    fun bind(isSelected: Boolean) {
        binding.iv.isSelected = isSelected
        binding.root.isSelected = isSelected
    }

    fun clear() {
        imageLoader.clear(binding.iv)
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            imageLoader: ImageLoader,
            onSpellClickListener: (spell: VOSpell) -> Unit,
        ): SpellViewHolder {
            val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
            return SpellViewHolder(binding, imageLoader, onSpellClickListener)
        }
    }

}