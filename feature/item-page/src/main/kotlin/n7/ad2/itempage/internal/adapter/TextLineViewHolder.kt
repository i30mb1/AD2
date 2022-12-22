package n7.ad2.itempage.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.itempage.databinding.ItemTextLineBinding
import n7.ad2.itempage.internal.domain.vo.VOItemInfo

class TextLineViewHolder private constructor(
    private val binding: ItemTextLineBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOItemInfo.TextLine) {
        binding.tvText.text = item.title
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): TextLineViewHolder {
            val binding = ItemTextLineBinding.inflate(layoutInflater, parent, false)
            return TextLineViewHolder(binding)
        }
    }

}