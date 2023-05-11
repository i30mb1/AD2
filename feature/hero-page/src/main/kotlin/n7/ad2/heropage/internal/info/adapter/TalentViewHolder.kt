package n7.ad2.heropage.internal.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.load
import n7.ad2.feature.heropage.databinding.ItemSpellBinding
import n7.ad2.heropage.internal.info.domain.vo.VOSpell

class TalentViewHolder(
    private val binding: ItemSpellBinding,
    private val onSpellClickListener: (spell: VOSpell.Talent) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOSpell.Talent) {
        binding.iv.load(n7.ad2.core.ui.R.drawable.talent, n7.ad2.core.ui.R.drawable.square_placeholder, n7.ad2.core.ui.R.drawable.square_error_placeholder)
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
            onSpellClickListener: (spell: VOSpell.Talent) -> Unit,
        ): TalentViewHolder {
            val binding = ItemSpellBinding.inflate(layoutInflater, parent, false)
            return TalentViewHolder(binding, onSpellClickListener)
        }
    }

}