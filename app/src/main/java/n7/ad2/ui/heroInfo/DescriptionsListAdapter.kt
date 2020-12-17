package n7.ad2.ui.heroInfo

import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.databinding.ItemBodyHeroAttrsBinding
import n7.ad2.databinding.ItemBodyHeroSpellsBinding
import n7.ad2.databinding.ItemBodyRecipeBinding
import n7.ad2.databinding.ItemBodyWithImageBinding
import n7.ad2.databinding.ItemBodyWithSeparatorBinding
import n7.ad2.databinding.ItemTitleBinding
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodyRecipe
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroAttrs
import n7.ad2.ui.heroInfo.domain.vo.VOHeroSpells
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.itemInfo.RecipeImagesAdapter
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.extension.toPx
import kotlin.math.max

class DescriptionsListAdapter(
    private val audioExoPlayer: AudioExoPlayer,
    private val infoPopupWindow: InfoPopupWindow,
) : ListAdapter<VODescription, DescriptionsListAdapter.ViewHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    private val popupListener = { view: View, text: String -> infoPopupWindow.show(view, text) }
    private val descriptionsListener = { voDescriptions: List<VODescription> -> setDescriptions(voDescriptions) }

    override fun getHeaderLayout(): Int = R.layout.item_title

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOTitle -> R.layout.item_title
        is VOBodyLine -> R.layout.item_body_line
        is VOBodySimple -> R.layout.item_body_simple
        is VOBodyWithSeparator -> R.layout.item_body_with_separator
        is VOBodyWithImage -> R.layout.item_body_with_image
        is VOBodyTalent -> R.layout.item_body_talent
        is VOBodyRecipe -> R.layout.item_body_recipe
        is VOHeroAttrs -> R.layout.item_body_hero_attrs
        is VOHeroSpells -> R.layout.item_body_hero_spells
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent, viewType, audioExoPlayer, popupListener, descriptionsListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    @OptIn(ExperimentalStdlibApi::class)
    private fun setDescriptions(voDescriptions: List<VODescription>) {
        val newList = buildList {
            addAll(currentList.takeWhile { it !is VOTitle })
            addAll(voDescriptions)
        }
        submitList(newList)
    }

    class ViewHolder private constructor(
        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var lineHeight = 0

        fun bind(item: VODescription) {
            binding.setVariable(BR.item, item)
            bindSpecificVO(binding)
            binding.executePendingBindings()
        }

        private fun bindSpecificVO(binding: ViewDataBinding) {
            when (binding) {
                is ItemBodyRecipeBinding -> (binding.rv.layoutManager as GridLayoutManager).spanCount = max(1, binding.item!!.recipes.size)
                is ItemBodyWithSeparatorBinding -> setBoundToImageSpan(binding.tvBody, binding.item!!.body)
                is ItemBodyWithImageBinding -> setBoundToImageSpan(binding.tvBody, binding.item!!.body)
            }
        }

        private fun setBoundToImageSpan(
            tv: TextView,
            spannableString: SpannableString,
        ) {
            if (lineHeight == 0) lineHeight = tv.lineHeight - 2.toPx
            spannableString.getSpans<ImageSpan>().forEach { it.drawable.setBounds(0, 0, lineHeight, lineHeight) }
            spannableString.getSpans<PopUpClickableSpan>().forEach { it.popUpListener = popupListener }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewType: Int,
                audioExoPlayer: AudioExoPlayer,
                popupListener: (View, String) -> Unit,
                descriptionsListener: (List<VODescription>) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ViewDataBinding = when (viewType) {
                    R.layout.item_title -> ItemTitleBinding.inflate(layoutInflater, parent, false).also { it.audioExoPlayer = audioExoPlayer; it.popupListener = popupListener }
                    R.layout.item_body_recipe -> ItemBodyRecipeBinding.inflate(layoutInflater, parent, false).apply { rv.adapter = RecipeImagesAdapter() }
                    R.layout.item_body_hero_spells -> ItemBodyHeroSpellsBinding.inflate(layoutInflater, parent, false).apply { rv.adapter = SpellsListAdapter(descriptionsListener) }
                    R.layout.item_body_with_image -> ItemBodyWithImageBinding.inflate(layoutInflater, parent, false).also { it.popupListener = popupListener }
                    R.layout.item_body_hero_attrs -> ItemBodyHeroAttrsBinding.inflate(layoutInflater, parent, false).also { it.descriptionListener = descriptionsListener }
                    else -> DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
                }
                return ViewHolder(binding, popupListener)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VODescription>() {

        override fun areItemsTheSame(oldItem: VODescription, newItem: VODescription): Boolean {
            return when (oldItem) {
                is VOTitle -> newItem is VOTitle
                is VOBodySimple -> newItem is VOBodySimple
                is VOBodyWithSeparator -> newItem is VOBodyWithSeparator
                is VOBodyWithImage -> newItem is VOBodyWithImage
                is VOBodyLine -> newItem is VOBodyLine
                is VOBodyTalent -> newItem is VOBodyTalent
                is VOBodyRecipe -> newItem is VOBodyRecipe
                is VOHeroAttrs -> newItem is VOHeroAttrs
                is VOHeroSpells -> newItem is VOHeroSpells
            }
        }

        override fun areContentsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem == newItem
    }

}


