package n7.ad2.heropage.internal.responses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.load
import n7.ad2.heropage.databinding.ItemResponseImageBinding
import n7.ad2.heropage.internal.responses.domain.vo.VOResponseImage

class ImageViewHolder(
    private val binding: ItemResponseImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: VOResponseImage, showPopup: () -> Unit) {
        binding.root.load(model.imageUrl)
        binding.root.setOnClickListener { showPopup() }
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): ImageViewHolder {
            val binding = ItemResponseImageBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }
    }
}