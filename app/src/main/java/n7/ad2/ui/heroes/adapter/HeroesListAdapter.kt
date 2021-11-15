package n7.ad2.ui.heroes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.base.adapter.HeaderViewHolder
import n7.ad2.ui.heroes.domain.vo.VOHero
import n7.ad2.ui.heroes.domain.vo.VOHeroBody
import n7.ad2.ui.heroes.domain.vo.VOHeroHeader
import n7.ad2.utils.ImageLoader

class HeroesListAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ImageLoader,
    onHeroClick: (hero: VOHeroBody) -> Unit,
) : ListAdapter<VOHero, RecyclerView.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener { view ->
        val hero = view.getTag(R.id.ViewHolderModel) as VOHeroBody
        onHeroClick(hero)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOHeroBody -> R.layout.item_hero_body
        is VOHeroHeader -> R.layout.item_header
        else -> throw UnsupportedOperationException("could not get type for item ${getItem(position)}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_hero_body -> HeroBodyViewHolder.from(layoutInflater, parent, imageLoader, listener)
        R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        else -> throw UnsupportedOperationException("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeroBodyViewHolder -> if (item != null) holder.bind(item as VOHeroBody) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as VOHeroHeader).data) else holder.clear()
            else -> throw UnsupportedOperationException("could not bind for $holder")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is HeroBodyViewHolder -> holder.clear()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOHero>() {
        override fun areItemsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem == newItem
    }

}

