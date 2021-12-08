package n7.ad2.hero_page.internal.responses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.ItemResponseImageBinding
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseImage

class ResponsesImagesAdapter(
    private val infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
) : RecyclerView.Adapter<ResponsesImagesAdapter.ImageViewHolder>() {

    var list = emptyList<VOResponseImage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder = ImageViewHolder.from(parent, infoPopupWindow)
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(list[position])
    override fun getItemViewType(position: Int): Int = R.layout.item_response_image
    override fun getItemCount(): Int = list.size

    class ImageViewHolder(
        private val binding: ItemResponseImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VOResponseImage) {

        }

        companion object {
            fun from(
                parent: ViewGroup,
                infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
            ): ImageViewHolder {
                val binding = ItemResponseImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                binding.infoPopupWindow = infoPopupWindow
                return ImageViewHolder(binding)
            }
        }
    }
}