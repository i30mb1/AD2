package n7.ad2.ui.heroes.adapter

import ad2.n7.android.extension.clear
import ad2.n7.android.extension.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemHeroBodyBinding
import n7.ad2.ui.heroes.domain.vo.VOHeroBody

class HeroBodyViewHolder private constructor(
    private val binding: ItemHeroBodyBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(hero: VOHeroBody) = binding.apply {
        ivImage.load(hero.imageUrl, R.drawable.stream_placeholder)
        tvTitle.text = hero.name
        binding.ivImage.isSelected = hero.viewedByUser
        root.setTag(R.id.ViewHolderBinding, binding)
        root.setTag(R.id.ViewHolderModel, hero)
    }

    fun clear() = binding.apply {
        ivImage.clear()
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            listener: View.OnClickListener,
        ): HeroBodyViewHolder {
            val binding = ItemHeroBodyBinding.inflate(layoutInflater, parent, false).apply {
                root.setOnClickListener(listener)
            }
            return HeroBodyViewHolder(binding)
        }
    }

}