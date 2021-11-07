package n7.ad2.ui.heroes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemHeroBinding
import n7.ad2.ui.heroes.domain.vo.VOHero
import n7.ad2.utils.ImageLoader

class HeroesListAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ImageLoader,
    onHeroClick: (hero: VOHero) -> Unit,
) : ListAdapter<VOHero, HeroesListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener { view ->
        val hero = view.getTag(R.id.ViewHolderModel) as VOHero
        onHeroClick(hero)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_hero

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(layoutInflater, parent, imageLoader, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = getItem(position)
        if (hero != null) holder.bindTo(hero) else holder.clear()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    class ViewHolder(
        private val binding: ItemHeroBinding,
        private val imageLoader: ImageLoader,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: VOHero) = binding.apply {
            imageLoader.load(ivImage, hero.imageUrl, R.drawable.hero_placeholder)
            tvTitle.text = hero.name
            vRedLine.isVisible = hero.viewedByUser
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
            ): ViewHolder {
                val binding = ItemHeroBinding.inflate(layoutInflater, parent, false).apply {
                    root.setOnClickListener(listener)
                }
                return ViewHolder(binding, imageLoader)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOHero>() {
        override fun areItemsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem.viewedByUser == newItem.viewedByUser
    }
}

