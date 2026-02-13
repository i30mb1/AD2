package n7.ad2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.core.ui.databinding.ItemHeaderBinding

class HeaderViewHolder private constructor(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    data class Data(val title: String)

    fun bind(item: Data) = binding.apply {
        tvTitle.text = item.title
    }

    fun clear() = Unit

    companion object {
        fun from(layoutInflater: LayoutInflater, parent: ViewGroup): HeaderViewHolder {
            val binding = ItemHeaderBinding.inflate(layoutInflater, parent, false)
            return HeaderViewHolder(binding)
        }
    }
}
