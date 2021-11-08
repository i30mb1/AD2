package n7.ad2.ui.items.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemItemHeaderBinding
import n7.ad2.ui.items.domain.vo.VOItemHeader

class ItemHeaderViewHolder private constructor(
    private val binding: ItemItemHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: VOItemHeader) = binding.apply {
        tvTitle.text = model.title
    }

    fun clear() = Unit

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): ItemHeaderViewHolder {
            val binding = ItemItemHeaderBinding.inflate(layoutInflater, parent, false)
            return ItemHeaderViewHolder(binding)
        }
    }
}