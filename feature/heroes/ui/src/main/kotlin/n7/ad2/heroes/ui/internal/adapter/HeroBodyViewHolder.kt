package n7.ad2.heroes.ui.internal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.clear
import n7.ad2.android.extension.load
import n7.ad2.feature.heroes.ui.R
import n7.ad2.feature.heroes.ui.databinding.ItemHeroBodyBinding
import n7.ad2.heroes.ui.internal.domain.vo.VOHero

internal class HeroBodyViewHolder private constructor(private val binding: ItemHeroBodyBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(hero: VOHero.Body) = binding.apply {
        ivImage.load(hero.imageUrl, R.drawable.stream_placeholder)
        tvTitle.text = hero.name
        ivImage.isSelected = hero.viewedByUser
        root.setTag(n7.ad2.core.ui.R.id.ViewHolderBinding, binding)
        root.setTag(n7.ad2.core.ui.R.id.ViewHolderModel, hero)
    }

    fun clear() = binding.apply {
        ivImage.clear()
    }

    companion object {
        fun from(layoutInflater: LayoutInflater, parent: ViewGroup, listener: View.OnClickListener): HeroBodyViewHolder {
            val binding = ItemHeroBodyBinding.inflate(layoutInflater, parent, false).apply {
                root.setOnClickListener(listener)
            }
            return HeroBodyViewHolder(binding)
        }
    }
}
