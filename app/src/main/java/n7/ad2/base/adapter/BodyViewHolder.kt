package n7.ad2.base.adapter

import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.getSpans
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemBodyBinding
import n7.ad2.utils.extension.toPx
import n7.ad2.utils.lazyUnsafe

class BodyViewHolder private constructor(
    private val binding: ItemBodyBinding,
) : RecyclerView.ViewHolder(binding.root) {

    @JvmInline
    value class Data(val text: Spanned)

    private val lineHeight by lazyUnsafe { binding.tvText.lineHeight - 2.toPx }

    fun bind(item: Data) = binding.apply {
        item.text.getSpans<ImageSpan>().forEach { it.drawable?.setBounds(0, 0, lineHeight, lineHeight) }
        tvText.text = item.text
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): BodyViewHolder {
            val binding = ItemBodyBinding.inflate(layoutInflater, parent, false)
            return BodyViewHolder(binding)
        }
    }

}