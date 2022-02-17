package n7.ad2.item_page.internal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.vo.VOItemInfo
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderComplexViewHolder
import n7.ad2.ui.adapter.ImageLineViewHolder

class ItemInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val onPlayIconClick: (soundUrl: String) -> Unit,
    private val onQuickKeyClick: (key: String) -> Unit,
) : ListAdapter<VOItemInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    private val popupListener = { view: View, text: String -> }

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is VOItemInfo.Title -> R.layout.item_header_complex
        is VOItemInfo.TextLine -> R.layout.item_text_line
        is VOItemInfo.Recipe -> R.layout.item_info_recipe
        is VOItemInfo.Body -> R.layout.item_body
        is VOItemInfo.ImageLine -> R.layout.item_image_line
        else -> error("could not getItemViewType for $item")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_text_line -> TextLineViewHolder.from(layoutInflater, parent)
        R.layout.item_info_recipe -> InfoRecipeViewHolder.from(layoutInflater, parent)
        R.layout.item_body -> BodyViewHolder.from(layoutInflater, parent)
        R.layout.item_header_complex -> HeaderComplexViewHolder.from(layoutInflater, parent, onPlayIconClick, onQuickKeyClick)
        R.layout.item_image_line -> ImageLineViewHolder.from(layoutInflater, parent, {})
        else -> error("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextLineViewHolder -> if (item != null) holder.bind(item as VOItemInfo.TextLine)
            is InfoRecipeViewHolder -> if (item != null) holder.bind(item as VOItemInfo.Recipe)
            is BodyViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Body).data)
            is HeaderComplexViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Title).data)
            is ImageLineViewHolder -> if (item != null) holder.bind((item as VOItemInfo.ImageLine).data)
            else -> error("could not bind for $holder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNullOrEmpty() -> super.onBindViewHolder(holder, position, payloads)
            holder is HeaderComplexViewHolder -> holder.bind((payloads.last() as VOItemInfo.Title).data.isPlaying)
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItemInfo>() {
        override fun areItemsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem == newItem
        override fun getChangePayload(oldItem: VOItemInfo, newItem: VOItemInfo): Any? {
            if (oldItem is VOItemInfo.Title && newItem is VOItemInfo.Title && oldItem.data.isPlaying != newItem.data.isPlaying) return newItem
            return super.getChangePayload(oldItem, newItem)
        }
    }

}