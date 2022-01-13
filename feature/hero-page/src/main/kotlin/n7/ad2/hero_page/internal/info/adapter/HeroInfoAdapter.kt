package n7.ad2.hero_page.internal.info.adapter

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.R
import n7.ad2.hero_page.internal.StickyHeaderDecorator
import n7.ad2.hero_page.internal.info.InfoPopupWindow
import n7.ad2.hero_page.internal.info.PopUpClickableSpan
import n7.ad2.hero_page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero_page.internal.info.domain.vo.VOBodyLine
import n7.ad2.hero_page.internal.info.domain.vo.VOBodySimple
import n7.ad2.hero_page.internal.info.domain.vo.VOBodyTalent
import n7.ad2.hero_page.internal.info.domain.vo.VOBodyWithImage
import n7.ad2.hero_page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero_page.internal.info.domain.vo.VOSpell
import n7.ad2.ktx.dpToPx
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderComplexViewHolder
import n7.ad2.ui.adapter.HeaderViewHolder

class HeroInfoAdapter(
    private val layoutInflater: LayoutInflater,
    private val infoPopupWindow: InfoPopupWindow,
    private val onPlayIconClickListener: (soundUrl: String) -> Unit,
    private val onKeyClickListener: (key: String) -> Unit,
    private val onHeroInfoCLickListener: (heroInfo: GetVOHeroDescriptionUseCase.HeroInfo) -> Unit,
    private val onSpellClickListener: (spell: VOSpell) -> Unit,
) : ListAdapter<VOHeroInfo, RecyclerView.ViewHolder>(DiffCallback()),
    StickyHeaderDecorator.StickyHeaderInterface {

    override fun getHeaderLayout(): Int = R.layout.item_header

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VOHeroInfo.HeaderSound -> R.layout.item_header_sound
        is VOHeroInfo.Attributes -> R.layout.item_hero_attributes
        is VOHeroInfo.Header -> R.layout.item_header
        is VOHeroInfo.Body -> R.layout.item_body
        is VOHeroInfo.Spells -> R.layout.item_hero_spells

        is VOBodyLine -> R.layout.item_body_line
        is VOBodySimple -> R.layout.item_body_simple
        is VOBodyWithImage -> R.layout.item_body_with_image
        is VOBodyTalent -> R.layout.item_body_talent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_header_sound -> HeaderComplexViewHolder.from(layoutInflater, parent, onPlayIconClickListener, onKeyClickListener)
        R.layout.item_hero_attributes -> HeroInfoMainViewHolder.from(layoutInflater, parent, onHeroInfoCLickListener)
        R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        R.layout.item_body -> BodyViewHolder.from(layoutInflater, parent)
        R.layout.item_hero_spells -> HeroSpellsViewHolder.from(layoutInflater, parent, onSpellClickListener)
        else -> throw UnsupportedOperationException("could not find ViewHolder for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
//            is HeaderComplexViewHolder -> if (item != null) holder.bind(item as VOHeroInfo.HeaderSound) else holder.clear()
            is HeroInfoMainViewHolder -> if (item != null) holder.bind(item as VOHeroInfo.Attributes) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as VOHeroInfo.Header).item) else holder.clear()
            is BodyViewHolder -> if (item != null) holder.bind((item as VOHeroInfo.Body).item) else holder.clear()
            is HeroSpellsViewHolder -> if (item != null) holder.bind(item as VOHeroInfo.Spells) else holder.clear()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isNullOrEmpty() -> super.onBindViewHolder(holder, position, payloads)
            holder is HeroSpellsViewHolder -> holder.bind((payloads.last() as VOHeroInfo.Spells))
            holder is HeroInfoMainViewHolder -> holder.bind((payloads.last() as VOHeroInfo.Attributes).isSelected)
        }
    }

    private fun setDescriptions(voDescriptions: List<VOHeroInfo>) = submitList(buildList {
        addAll(currentList.takeWhile { it !is VOHeroInfo.HeaderSound })
        addAll(voDescriptions)
    })

    class ViewHolder private constructor(
//        private val binding: ViewDataBinding,
        private val popupListener: (View, String) -> Unit,
    ) : RecyclerView.ViewHolder(TODO()) {

        private var lineHeight = 0

        fun bind(item: VOHeroInfo) {
//            binding.setVariable(BR.item, item)
//            bindSetting(binding)
//            binding.executePendingBindings()
        }

        private fun bindSetting() {
//            when (binding) {
//                is ItemBodyWithImageBinding -> setActionsForSpans(binding.tvBody, binding.item!!.body)
//                is ItemBodyLineBinding -> setActionsForSpans(binding.tvBody, binding.item!!.title)
//            }
        }

        private fun setActionsForSpans(
            tv: TextView,
            spannableString: SpannableString,
        ) {
            if (lineHeight == 0) lineHeight = tv.lineHeight - 2.dpToPx
            spannableString.getSpans<ImageSpan>().forEach { it.drawable.setBounds(0, 0, lineHeight, lineHeight) }
            spannableString.getSpans<PopUpClickableSpan>().forEach { it.popupListener = popupListener }
            tv.movementMethod = LinkMovementMethod.getInstance()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewType: Int,
//                audioExoPlayer: AudioExoPlayer,
                popupListener: (View, String) -> Unit,
                descriptionsListener: (List<VOHeroInfo>) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding: ViewDataBinding = when (viewType) {
//                    R.layout.item_body_with_image -> ItemBodyWithImageBinding.inflate(layoutInflater, parent, false).also { it.popupListener = popupListener }
//                    else -> DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
//                }
                return TODO()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOHeroInfo>() {
        override fun areItemsTheSame(oldItem: VOHeroInfo, newItem: VOHeroInfo): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOHeroInfo, newItem: VOHeroInfo): Boolean = oldItem == newItem
        override fun getChangePayload(oldItem: VOHeroInfo, newItem: VOHeroInfo): Any? {
            if (oldItem is VOHeroInfo.Spells && newItem is VOHeroInfo.Spells && oldItem.spells != newItem.spells) return newItem
            if (oldItem is VOHeroInfo.Attributes && newItem is VOHeroInfo.Attributes && oldItem.isSelected != newItem.isSelected) return newItem
            return super.getChangePayload(oldItem, newItem)
        }
    }
}