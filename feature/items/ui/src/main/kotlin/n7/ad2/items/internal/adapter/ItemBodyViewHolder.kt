package n7.ad2.items.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.clear
import n7.ad2.android.extension.load
import n7.ad2.feature.items.ui.R
import n7.ad2.feature.items.ui.databinding.ItemItemBodyBinding
import n7.ad2.items.internal.model.ItemUI

internal class ItemBodyViewHolder private constructor(
    private val binding: ItemItemBodyBinding,
    private val itemClickListener: (model: ItemUI.Body, view: ImageView) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ItemUI.Body) = binding.apply {
        ivImage.load(model.imageUrl, R.drawable.item_placeholder)
        tvTitle.text = model.name
        ivImage.isSelected = model.viewedByUser
        ivImage.transitionName = model.name
        root.setOnClickListener { itemClickListener.invoke(model, ivImage) }
    }

    fun clear() = binding.apply {
        ivImage.clear()
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            clickListener: (model: ItemUI.Body, view: ImageView) -> Unit,
        ): ItemBodyViewHolder {
            val binding = ItemItemBodyBinding.inflate(layoutInflater, parent, false)
            return ItemBodyViewHolder(binding, clickListener)
        }
    }

}
