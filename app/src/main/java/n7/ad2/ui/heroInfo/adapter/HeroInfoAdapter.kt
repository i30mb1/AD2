package n7.ad2.ui.heroInfo.adapter

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
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroInfo.PopUpClickableSpan
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoHeaderSound
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoMain
import n7.ad2.ui.heroInfo.domain.vo.VOHeroSpells
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.utils.ImageLoader
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.extension.toPx

class HeroInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val audioExoPlayer: AudioExoPlayer,
    private val infoPopupWindow: InfoPopupWindow,
    private val imageLoader: ImageLoader,
    private val onPlayIconClickListener: (model: VOHeroInfoHeaderSound) -> Unit,
    private val onKeyClickListener: (key: String) -> Unit,
) : ListAdapter<VOHeroInfo, RecyclerView.ViewHolder>(DiffCallback()),
    StickyHeaderDecorator.StickyHeaderInterface {

    private val popupListener = { view: View, text: String -> infoPopupWindow.show(view, text) }
    private val descriptionsListener = { voDescriptions: List<VOHeroInfo> -> setDescriptions(voDescriptions) }

    override fun getHeaderLayout(): Int = R.layout.item_header_sound

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOHeroInfoHeaderSound -> R.layout.item_header_sound
        is VOHeroInfoMain -> R.layout.item_hero_info_main

        is VOBodyLine -> R.layout.item_body_line
        is VOBodySimple -> R.layout.item_body_simple
        is VOBodyWithSeparator -> R.layout.item_body_with_separator
        is VOBodyWithImage -> R.layout.item_body_with_image
        is VOBodyTalent -> R.layout.item_body_talent
        is VOHeroSpells -> R.layout.item_body_hero_spells
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_header_sound -> HeaderSoundViewHolder.from(layoutInflater, parent, onPlayIconClickListener, onKeyClickListener)
        R.layout.item_hero_info_main -> HeroInfoMainViewHolder.from(layoutInflater, parent, imageLoader)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderSoundViewHolder -> if (item != null) holder.bind(item as VOHeroInfoHeaderSound) else holder.clear()
            is HeroInfoMainViewHolder -> if (item != null) holder.bind(item as VOHeroInfoMain) else holder.clear()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun setDescriptions(voDescriptions: List<VOHeroInfo>) = submitList(buildList {
        addAll(currentList.takeWhile { it !is VOHeroInfoHeaderSound })
        addAll(voDescriptions)
    })

    class ViewHolder private constructor(
        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var lineHeight = 0

        fun bind(item: VOHeroInfo) {
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
                descriptionsListener: (List<VOHeroInfo>) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ViewDataBinding = when (viewType) {
                    R.layout.item_body_hero_spells -> ItemBodyHeroSpellsBinding.inflate(layoutInflater, parent, false).apply {
                        val spellsListAdapter = SpellsListAdapter(descriptionsListener)
                        spellsListAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
                        rv.adapter = spellsListAdapter
                    }
                    R.layout.item_body_with_image -> ItemBodyWithImageBinding.inflate(layoutInflater, parent, false).also { it.popupListener = popupListener }
                    else -> DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
                }
                return ViewHolder(binding, popupListener)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOHeroInfo>() {
        override fun areItemsTheSame(oldItem: VOHeroInfo, newItem: VOHeroInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOHeroInfo, newItem: VOHeroInfo): Boolean = oldItem == newItem
    }
}