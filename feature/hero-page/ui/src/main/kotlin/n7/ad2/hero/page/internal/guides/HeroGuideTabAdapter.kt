package n7.ad2.hero.page.internal.guides

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideTab
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideTabNumber

class HeroGuideTabAdapter : ListAdapter<VOGuideTab, HeroGuideTabAdapter.HeroGuideTabHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeroGuideTabHolder.from(parent, viewType)

    override fun onBindViewHolder(holder: HeroGuideTabHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOGuideTabNumber -> R.layout.item_hero_guide_tab_number
    }

    class HeroGuideTabHolder private constructor(
//        private val binding: ViewDataBinding,
    ) : RecyclerView.ViewHolder(TODO()) {

        fun bind(item: VOGuideTab) {
//            binding.setVariable(BR.item, item)
//            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, layoutId: Int): HeroGuideTabHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false)
                return TODO()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOGuideTab>() {
        override fun areItemsTheSame(oldItem: VOGuideTab, newItem: VOGuideTab): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOGuideTab, newItem: VOGuideTab): Boolean = oldItem == newItem
    }
}
