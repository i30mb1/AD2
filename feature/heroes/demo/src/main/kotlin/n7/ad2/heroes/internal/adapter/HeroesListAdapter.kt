package n7.ad2.heroes.internal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.heroes.demo.R
import n7.ad2.heroes.internal.domain.vo.VOHero
import n7.ad2.ui.adapter.HeaderViewHolder

internal class HeroesListAdapter(
    private val layoutInflater: LayoutInflater,
    onHeroClick: (hero: VOHero.Body) -> Unit,
) : ListAdapter<VOHero, RecyclerView.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener { view ->
        val hero = view.getTag(n7.ad2.core.ui.R.id.ViewHolderModel) as VOHero.Body
        onHeroClick(hero)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is VOHero.Body -> R.layout.item_hero_body
        is VOHero.Header -> n7.ad2.core.ui.R.layout.item_header
        else -> throw UnsupportedOperationException("could not get type for item ${getItem(position)}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_hero_body -> HeroBodyViewHolder.from(layoutInflater, parent, listener)
        n7.ad2.core.ui.R.layout.item_header -> HeaderViewHolder.from(layoutInflater, parent)
        else -> throw UnsupportedOperationException("could not get type for $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeroBodyViewHolder -> if (item != null) holder.bind(item as VOHero.Body) else holder.clear()
            is HeaderViewHolder -> if (item != null) holder.bind((item as VOHero.Header).data) else holder.clear()
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

