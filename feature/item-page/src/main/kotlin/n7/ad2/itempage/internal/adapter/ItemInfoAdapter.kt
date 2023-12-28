package n7.ad2.itempage.internal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.item.page.R
import n7.ad2.itempage.internal.domain.vo.VOItemInfo
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderPlayableViewHolder
import n7.ad2.ui.adapter.ImageLineViewHolder

class ItemInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val onPlayIconClick: (soundUrl: String) -> Unit,
    private val showPopup: (view: View, text: String) -> Unit,
) : ListAdapter<VOItemInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is VOItemInfo.Title -> n7.ad2.core.ui.R.layout.item_header_playable
        is VOItemInfo.TextLine -> R.layout.item_text_line
        is VOItemInfo.Recipe -> R.layout.item_info_recipe
        is VOItemInfo.Body -> n7.ad2.core.ui.R.layout.item_body
        is VOItemInfo.ImageLine -> n7.ad2.core.ui.R.layout.item_image_line
        else -> error("could not getItemViewType for $item")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_text_line -> TextLineViewHolder.from(layoutInflater, parent)
        R.layout.item_info_recipe -> InfoRecipeViewHolder.from(layoutInflater, parent)
        n7.ad2.core.ui.R.layout.item_body -> BodyViewHolder.from(layoutInflater, parent)
        n7.ad2.core.ui.R.layout.item_header_playable -> HeaderPlayableViewHolder.from(layoutInflater, parent, onPlayIconClick)
        n7.ad2.core.ui.R.layout.item_image_line -> ImageLineViewHolder.from(layoutInflater, parent, showPopup)
        else -> error("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextLineViewHolder -> if (item != null) holder.bind(item as VOItemInfo.TextLine)
            is InfoRecipeViewHolder -> if (item != null) holder.bind(item as VOItemInfo.Recipe)
            is BodyViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Body).data)
            is HeaderPlayableViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Title).data)
            is ImageLineViewHolder -> if (item != null) holder.bind((item as VOItemInfo.ImageLine).data)
            else -> error("could not bind for $holder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNullOrEmpty() -> super.onBindViewHolder(holder, position, payloads)
            holder is HeaderPlayableViewHolder -> holder.bind((payloads.last() as VOItemInfo.Title).data.isPlaying)
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
