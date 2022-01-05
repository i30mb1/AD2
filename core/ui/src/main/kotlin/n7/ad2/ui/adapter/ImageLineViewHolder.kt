package n7.ad2.ui.adapter

import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ui.databinding.ItemImageLineBinding

class ImageLineViewHolder private constructor(
    private val binding: ItemImageLineBinding,
    private val onImageClick: (text: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    data class Data(val text: Spanned, @DrawableRes val drawable: Int)

    fun bind(item: Data) {
        binding.tv.text = item.text
        binding.iv.setImageResource(item.drawable)
    }

    fun clear() = Unit

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onImageClick: (text: String) -> Unit,
        ): ImageLineViewHolder {
            val binding = ItemImageLineBinding.inflate(layoutInflater, parent, false)
            return ImageLineViewHolder(binding, onImageClick)
        }
    }

}