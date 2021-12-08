package n7.ad2.ui.itemInfo

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.hero_page.internal.pager.AudioExoPlayer
import n7.ad2.ui.itemInfo.domain.vo.ItemInfo
import n7.ad2.ui.itemInfo.domain.vo.VOItemInfoBody
import n7.ad2.ui.itemInfo.domain.vo.VOItemInfoLine
import n7.ad2.ui.itemInfo.domain.vo.VOItemInfoLineImage
import n7.ad2.ui.itemInfo.domain.vo.VOItemInfoRecipe
import n7.ad2.ui.itemInfo.domain.vo.VOItemInfoTitle

class ItemInfoAdapter(
    private val audioExoPlayer: AudioExoPlayer,
    private val infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow,
) : ListAdapter<ItemInfo, ItemInfoAdapter.ViewHolder>(DiffCallback()), n7.ad2.hero_page.internal.StickyHeaderDecorator.StickyHeaderInterface {

    private val popupListener = { view: View, text: String -> infoPopupWindow.show(view, text) }

    override fun getHeaderLayout(): Int = R.layout.item_item_info_title

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOItemInfoTitle -> R.layout.item_item_info_title
        is VOItemInfoLine -> R.layout.item_item_info_line
        is VOItemInfoRecipe -> R.layout.item_item_info_recipe
        is VOItemInfoBody -> R.layout.item_item_info_body
        is VOItemInfoLineImage -> R.layout.item_item_info_line_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent, viewType)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(
//        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(TODO()) {

        fun bind(item: ItemInfo) {
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

    private class DiffCallback : DiffUtil.ItemCallback<ItemInfo>() {
        override fun areItemsTheSame(oldItem: ItemInfo, newItem: ItemInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: ItemInfo, newItem: ItemInfo): Boolean = oldItem == newItem
    }
}