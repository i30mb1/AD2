package n7.ad2.item_page.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.vo.VOItemInfo
import n7.ad2.media_player.AudioExoPlayer
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderComplexViewHolder

class ItemInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val audioExoPlayer: AudioExoPlayer,
    private val onPlayIconClickListener: (model: HeaderComplexViewHolder.Data) -> Unit,
    private val onQuickKeyClickListener: (key: String) -> Unit,
//    private val infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
) : ListAdapter<VOItemInfo, RecyclerView.ViewHolder>(DiffCallback()) {
//    StickyHeaderDecorator.StickyHeaderInterface

//    private val popupListener = { view: View, text: String -> infoPopupWindow.show(view, text) }
//
//    override fun getHeaderLayout(): Int = R.layout.item_item_info_title

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOItemInfo.Title -> R.layout.item_header_complex
        is VOItemInfo.TextLine -> R.layout.item_text_line
        is VOItemInfo.Recipe -> R.layout.item_info_recipe
        is VOItemInfo.Body -> R.layout.item_body
//        is VOItemInfoLineImage -> R.layout.item_item_info_line_image
        else -> TODO()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_text_line -> TextLineViewHolder.from(layoutInflater, parent)
        R.layout.item_info_recipe -> InfoRecipeViewHolder.from(layoutInflater, parent)
        R.layout.item_body -> BodyViewHolder.from(layoutInflater, parent)
        R.layout.item_header_complex -> HeaderComplexViewHolder.from(layoutInflater, parent, onPlayIconClickListener, onQuickKeyClickListener)
        else -> throw UnsupportedOperationException("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextLineViewHolder -> if (item != null) holder.bind(item as VOItemInfo.TextLine)
            is InfoRecipeViewHolder -> if (item != null) holder.bind(item as VOItemInfo.Recipe)
            is BodyViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Body).data)
            is HeaderComplexViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Title).data)
            else -> throw UnsupportedOperationException("could not bind for $holder")
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItemInfo>() {
        override fun areItemsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem == newItem
    }

}