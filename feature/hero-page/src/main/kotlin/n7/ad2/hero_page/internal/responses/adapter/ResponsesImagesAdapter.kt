package n7.ad2.hero_page.internal.responses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.R
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseImage

class ResponsesImagesAdapter(
    private val layoutInflater: LayoutInflater,
    private val showPopup: () -> Unit,
) : RecyclerView.Adapter<ImageViewHolder>() {

    var list = emptyList<VOResponseImage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder = ImageViewHolder.from(layoutInflater, parent)

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(list[position], showPopup)

    override fun getItemViewType(position: Int): Int = R.layout.item_response_image

    override fun getItemCount(): Int = list.size

}