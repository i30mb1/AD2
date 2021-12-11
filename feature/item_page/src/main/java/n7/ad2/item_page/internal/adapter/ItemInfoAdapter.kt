package n7.ad2.item_page.internal.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.vo.VOItemInfo
import n7.ad2.media_player.AudioExoPlayer
import n7.ad2.ui.adapter.BodyViewHolder

class ItemInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val audioExoPlayer: AudioExoPlayer,
//    private val infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
) : ListAdapter<VOItemInfo, RecyclerView.ViewHolder>(DiffCallback()) {
//    StickyHeaderDecorator.StickyHeaderInterface

//    private val popupListener = { view: View, text: String -> infoPopupWindow.show(view, text) }
//
//    override fun getHeaderLayout(): Int = R.layout.item_item_info_title

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
//        is VOItemInfoTitle -> R.layout.item_item_info_title
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
        else -> throw UnsupportedOperationException("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextLineViewHolder -> if (item != null) holder.bind(item as VOItemInfo.TextLine)
            is InfoRecipeViewHolder -> if (item != null) holder.bind(item as VOItemInfo.Recipe)
            is BodyViewHolder -> if (item != null) holder.bind((item as VOItemInfo.Body).data)
            else -> throw UnsupportedOperationException("could not bind for $holder")
        }
    }

    class ViewHolder private constructor(
//        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(TODO()) {

        fun bind(item: VOItemInfo) {
//            binding.setVariable(BR.item, item)
//            bindSetting(binding)
//            binding.executePendingBindings()
        }

        private fun bindSetting(binding: Unit) {
            when (binding) {
//                is ItemItemInfoRecipeBinding -> (binding.rv.layoutManager as GridLayoutManager).spanCount = max(1, binding.item!!.recipes.size)
//                is ItemItemInfoLineImageBinding -> setPopupListener(binding.item!!.body)
            }
        }

        private fun setPopupListener(spannableString: SpannableString) {
//            spannableString.getSpans<n7.ad2.hero_page.internal.info.PopUpClickableSpan>().forEach { it.popupListener = popupListener }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewType: Int,
//                audioExoPlayer: n7.ad2.hero_page.AudioExoPlayer,
//                popupListener: (View, String) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding: ViewDataBinding = when (viewType) {
//                    R.layout.item_item_info_title -> ItemItemInfoTitleBinding.inflate(layoutInflater, parent, false).also { it.audioExoPlayer = audioExoPlayer }
//                    R.layout.item_item_info_recipe -> ItemItemInfoRecipeBinding.inflate(layoutInflater, parent, false).apply { rv.adapter = RecipeImagesAdapter() }
//                    R.layout.item_item_info_line_image -> ItemItemInfoLineImageBinding.inflate(layoutInflater, parent, false).also { it.popupListener = popupListener }
//                    else -> DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
//                }
                return TODO()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOItemInfo>() {
        override fun areItemsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOItemInfo, newItem: VOItemInfo): Boolean = oldItem == newItem
    }
}