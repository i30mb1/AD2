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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.base.VOPopUpListener
import n7.ad2.databinding.ItemBodyWithImageBinding
import n7.ad2.databinding.ItemBodyWithSeparatorBinding
import n7.ad2.databinding.ItemTitleWithIconBinding
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitleSimple
import n7.ad2.ui.heroInfo.domain.vo.VOTitleWithIcon
import n7.ad2.utils.extension.toPx

class DescriptionsListAdapter(private val fragment: HeroInfoFragment) : ListAdapter<VODescription, DescriptionsListAdapter.ViewHolder>(DiffCallback()) {

    private var listener: VOPopUpListener<String> = object : VOPopUpListener<String> {

        override fun onClickListener(view: View, text: String) {
            fragment.showPopup(view, text)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VOTitleWithIcon -> R.layout.item_title_with_icon
            is VOBodyLine -> R.layout.item_body_line
            is VOBodySimple -> R.layout.item_body_simple
            is VOTitleSimple -> R.layout.item_title_simple
            is VOBodyWithSeparator -> R.layout.item_body_with_separator
            is VOBodyWithImage -> R.layout.item_body_with_image
            is VOBodyTalent -> R.layout.item_body_talent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewType, listener, fragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(
            private val binding: ViewDataBinding,
            private val listener: VOPopUpListener<String>,
            private val fragment: HeroInfoFragment
    ) : RecyclerView.ViewHolder(binding.root) {

        private var lineHeight = 0

        fun bind(item: VODescription) {
            bindSpecificVO(binding, item)
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }

        private fun bindSpecificVO(binding: ViewDataBinding, item: VODescription) {
            when (binding) {
                is ItemBodyWithSeparatorBinding -> setBoundToImageSpan(binding.tvBody, (item as VOBodyWithSeparator).body)
                is ItemBodyWithImageBinding -> { setBoundToImageSpan(binding.tvBody, (item as VOBodyWithImage).body); binding.listener = listener }
                is ItemTitleWithIconBinding -> binding.let { it.fragment = fragment; it.listener = listener }
            }
        }

        private fun setBoundToImageSpan(tv: TextView, spannableString: SpannableString) {
            if (lineHeight == 0) lineHeight = tv.lineHeight - 2.toPx.toInt()
            spannableString.getSpans<ImageSpan>().forEach { it.drawable.setBounds(0, 0, lineHeight, lineHeight) }
        }

        companion object {
            fun from(parent: ViewGroup,
                     viewType: Int,
                     listener: VOPopUpListener<String>,
                     fragment: HeroInfoFragment): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
                return ViewHolder(binding, listener, fragment)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VODescription>() {

        override fun areItemsTheSame(oldItem: VODescription, newItem: VODescription): Boolean {
            return when (oldItem) {
                is VOTitleWithIcon -> newItem is VOTitleWithIcon
                is VOTitleSimple -> newItem is VOTitleSimple
                is VOBodySimple -> newItem is VOBodySimple
                is VOBodyWithSeparator -> newItem is VOBodyWithSeparator
                is VOBodyWithImage -> newItem is VOBodyWithImage
                is VOBodyLine -> newItem is VOBodyLine
                is VOBodyTalent -> newItem is VOBodyTalent
            }
        }

        override fun areContentsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = false
    }
}


