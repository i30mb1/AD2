package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemHeroInfoMainBinding
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoMain
import n7.ad2.utils.ImageLoader

class HeroInfoMainViewHolder private constructor(
    private val binding: ItemHeroInfoMainBinding,
    private val imageLoader: ImageLoader,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOHeroInfoMain) {
        imageLoader.load(binding.ivHero, item.urlHeroImage, R.drawable.hero_placeholder)
        binding.heroStatistics.setHeroStatistics(item.heroStatistics)
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            imageLoader: ImageLoader,
        ): HeroInfoMainViewHolder {
            val binding = ItemHeroInfoMainBinding.inflate(layoutInflater, parent, false)
            return HeroInfoMainViewHolder(binding, imageLoader)
        }
    }

}