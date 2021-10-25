package n7.ad2.ui.heroes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import n7.ad2.R
import n7.ad2.databinding.ItemHeroBinding
import n7.ad2.ui.heroes.domain.vo.VOHero

class HeroesListAdapter internal constructor(fragment: HeroesFragment) : ListAdapter<VOHero, HeroesListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener {
        fragment.startHeroFragment(
            it.getTag(R.id.ViewHolderModel) as VOHero,
            it.getTag(R.id.ViewHolderBinding) as ItemHeroBinding
        )
    }
    private val longListener = View.OnLongClickListener {
        false
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_hero

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener, longListener)

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
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: VOHero) = binding.apply {
            binding.model = hero
            executePendingBindings()
            root.setTag(R.id.ViewHolderBinding, binding)
            root.setTag(R.id.ViewHolderModel, hero)
        }

        fun clear() = binding.apply {
            iv.clear()
            tv.text = ""
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listener: View.OnClickListener,
                longListener: View.OnLongClickListener,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeroBinding.inflate(layoutInflater, parent, false).apply {
                    root.setOnClickListener(listener)
                    root.setOnLongClickListener(longListener)
                }
                return ViewHolder(binding)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOHero>() {
        override fun areItemsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: VOHero, newItem: VOHero) = oldItem.viewedByUser == newItem.viewedByUser
    }
}

