package n7.ad2.heropage.internal.guides

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.heropage.R
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideEasyToWinHeroes
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideHardToWinHeroes
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideHeroItems
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideInfoLine
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideItem
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideSpellBuild
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideStartingHeroItems
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideTitle
import n7.ad2.ui.StickyHeaderDecorator

class HeroGuideAdapter
    : ListAdapter<VOGuideItem, HeroGuideAdapter.HeroGuideHolder>(DiffCallback()), StickyHeaderDecorator.StickyHeaderInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TODO()

    override fun onBindViewHolder(holder: HeroGuideHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOGuideTitle -> R.layout.item_guide_title
        is VOGuideHardToWinHeroes -> R.layout.item_hard_to_win_heroes
        is VOGuideEasyToWinHeroes -> R.layout.item_easy_to_win_heroes
        is VOGuideInfoLine -> R.layout.item_guide_info_line
        is VOGuideSpellBuild -> R.layout.item_guide_skill_build
        is VOGuideStartingHeroItems -> R.layout.item_guide_starting_hero_items
        is VOGuideHeroItems -> R.layout.item_guide_hero_items
    }

    override fun getHeaderLayout() = R.layout.item_guide_title

    class HeroGuideHolder private constructor(

    ) : RecyclerView.ViewHolder(TODO()) {

        fun bind(item: VOGuideItem) {

        }

        companion object {
//            fun from(parent: ViewGroup, viewType: Int): HeroGuideHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
//                return HeroGuideHolder(binding)
//            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOGuideItem>() {
        override fun areItemsTheSame(oldItem: VOGuideItem, newItem: VOGuideItem): Boolean = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOGuideItem, newItem: VOGuideItem): Boolean = oldItem == newItem
    }

}