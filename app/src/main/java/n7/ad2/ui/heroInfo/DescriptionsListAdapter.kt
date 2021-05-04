package n7.ad2.ui.heroInfo

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.databinding.ItemBodyHeroSpellsBinding
import n7.ad2.databinding.ItemBodyLineBinding
import n7.ad2.databinding.ItemBodyWithImageBinding
import n7.ad2.databinding.ItemBodyWithSeparatorBinding
import n7.ad2.databinding.ItemHeroMainInformationBinding
import n7.ad2.databinding.ItemTitleBinding
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroMainInformation
import n7.ad2.ui.heroInfo.domain.vo.VOHeroSpells
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.extension.toPx

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
        is VOHeroMainInformation -> R.layout.item_hero_main_information
        is VOHeroSpells -> R.layout.item_body_hero_spells
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent, viewType, audioExoPlayer, popupListener, descriptionsListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    @OptIn(ExperimentalStdlibApi::class)
    private fun setDescriptions(voDescriptions: List<VODescription>) = submitList(buildList {
        addAll(currentList.takeWhile { it !is VOTitle })
        addAll(voDescriptions)
    })

    class ViewHolder private constructor(
        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var lineHeight = 0

        fun bind(item: VODescription) {
            binding.setVariable(BR.item, item)
            bindSetting(binding)
            binding.executePendingBindings()
        }

        private fun bindSetting(binding: ViewDataBinding) {
            when (binding) {
                is ItemBodyWithSeparatorBinding -> setActionsForSpans(binding.tvBody, binding.item!!.body)
                is ItemBodyWithImageBinding -> setActionsForSpans(binding.tvBody, binding.item!!.body)
                is ItemBodyLineBinding -> setActionsForSpans(binding.tvBody, binding.item!!.title)
            }
        }

        private fun setActionsForSpans(
            tv: TextView,
            spannableString: SpannableString,
        ) {
            if (lineHeight == 0) lineHeight = tv.lineHeight - 2.toPx
            spannableString.getSpans<ImageSpan>().forEach { it.drawable.setBounds(0, 0, lineHeight, lineHeight) }
            spannableString.getSpans<PopUpClickableSpan>().forEach { it.popupListener = popupListener }
            tv.movementMethod = LinkMovementMethod.getInstance()
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
                    R.layout.item_body_hero_spells -> ItemBodyHeroSpellsBinding.inflate(layoutInflater, parent, false).apply {
                        val spellsListAdapter = SpellsListAdapter(descriptionsListener)
                        spellsListAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
                        rv.adapter = spellsListAdapter
                    }
                    R.layout.item_body_with_image -> ItemBodyWithImageBinding.inflate(layoutInflater, parent, false).also { it.popupListener = popupListener }
                    R.layout.item_hero_main_information -> ItemHeroMainInformationBinding.inflate(layoutInflater, parent, false).also { it.descriptionListener = descriptionsListener }
                    else -> DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
                }
                return ViewHolder(binding, popupListener)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VODescription>() {
        override fun areItemsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem == newItem
    }
}