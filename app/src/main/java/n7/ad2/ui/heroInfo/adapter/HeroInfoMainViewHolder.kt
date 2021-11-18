package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemHeroAttributesBinding
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.utils.ImageLoader

class HeroInfoMainViewHolder private constructor(
    private val binding: ItemHeroAttributesBinding,
    private val imageLoader: ImageLoader,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOHeroInfo.Attributes) {
        imageLoader.load(binding.ivHero, item.urlHeroImage, R.drawable.hero_placeholder)
        binding.heroStatistics.setHeroStatistics(item.heroStatistics)
        bind(item.isSelected)
    }

    fun bind(isSelected: Boolean) {
        binding.root.isSelected = isSelected
        binding.ivHero.isSelected = isSelected
    }

    fun clear() = Unit

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            imageLoader: ImageLoader,
            onHeroInfoCLickListener: (heroInfo: GetVOHeroDescriptionUseCase.HeroInfo) -> Unit,
        ): HeroInfoMainViewHolder {
            val binding = ItemHeroAttributesBinding.inflate(layoutInflater, parent, false)
            binding.root.setOnClickListener { onHeroInfoCLickListener(GetVOHeroDescriptionUseCase.HeroInfo.Main) }
            return HeroInfoMainViewHolder(binding, imageLoader)
        }
    }

}