package n7.ad2.ui.items.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.android.extension.clear
import n7.ad2.android.extension.load
import n7.ad2.databinding.ItemItemBodyBinding
import n7.ad2.ui.items.domain.vo.VOItemBody

class ItemBodyViewHolder private constructor(
    private val binding: ItemItemBodyBinding,
    private val itemClickListener: (model: VOItemBody) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: VOItemBody) = binding.apply {
        ivImage.load(model.imageUrl, R.drawable.item_placeholder)
        tvTitle.text = model.name
        vRedLine.isVisible = model.viewedByUser
        root.setOnClickListener { itemClickListener.invoke(model) }
    }

    fun clear() = binding.apply {
        ivImage.clear()
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            clickListener: (model: VOItemBody) -> Unit,
        ): ItemBodyViewHolder {
            val binding = ItemItemBodyBinding.inflate(layoutInflater, parent, false)
            return ItemBodyViewHolder(binding, clickListener)
        }
    }

}