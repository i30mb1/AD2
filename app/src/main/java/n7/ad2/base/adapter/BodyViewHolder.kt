package n7.ad2.base.adapter

import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemBodyBinding

class BodyViewHolder private constructor(
    private val binding: ItemBodyBinding,
) : RecyclerView.ViewHolder(binding.root) {

    @JvmInline
    value class Data(val text: Spanned)

    fun bind(item: Data) = binding.apply {
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