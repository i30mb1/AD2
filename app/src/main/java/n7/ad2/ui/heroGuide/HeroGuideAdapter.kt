package n7.ad2.ui.heroGuide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.BR
import n7.ad2.R
import n7.ad2.ui.heroGuide.domain.vo.VOGuideBestVersus
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import n7.ad2.ui.heroGuide.domain.vo.VOGuideTitle
import n7.ad2.utils.StickyHeaderDecorator

class HeroGuideAdapter
    : ListAdapter<VOGuideItem, HeroGuideAdapter.HeroGuideHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeroGuideHolder.from(parent, viewType)

    override fun onBindViewHolder(holder: HeroGuideHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOGuideTitle -> R.layout.item_guide_title
        is VOGuideBestVersus -> R.layout.item_guide_best_versus
    }

    override fun getHeaderLayout() = R.layout.item_guide_title

    class HeroGuideHolder private constructor(
        private val binding: ViewDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VOGuideItem) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewType: Int): HeroGuideHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
                return HeroGuideHolder(binding)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOGuideItem>() {
        override fun areItemsTheSame(oldItem: VOGuideItem, newItem: VOGuideItem): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOGuideItem, newItem: VOGuideItem): Boolean = oldItem == newItem
    }

}