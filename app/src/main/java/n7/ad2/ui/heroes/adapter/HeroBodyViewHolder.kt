package n7.ad2.ui.heroes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemHeroBodyBinding
import n7.ad2.ui.heroes.domain.vo.VOHeroBody
import n7.ad2.utils.ImageLoader

class HeroBodyViewHolder private constructor(
    private val binding: ItemHeroBodyBinding,
    private val imageLoader: ImageLoader,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(hero: VOHeroBody) = binding.apply {
        imageLoader.load(ivImage, hero.imageUrl, R.drawable.hero_placeholder)
        tvTitle.text = hero.name
        binding.ivImage.isSelected = hero.viewedByUser
        root.setTag(R.id.ViewHolderBinding, binding)
        root.setTag(R.id.ViewHolderModel, hero)
    }

    fun clear() = binding.apply {
        imageLoader.clear(ivImage)
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            imageLoader: ImageLoader,
            listener: View.OnClickListener,
        ): HeroBodyViewHolder {
            val binding = ItemHeroBodyBinding.inflate(layoutInflater, parent, false).apply {
                root.setOnClickListener(listener)
            }
            return HeroBodyViewHolder(binding, imageLoader)
        }
    }

}