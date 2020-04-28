package n7.ad2.heroes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemListHeroBinding
import n7.ad2.heroes.db.HeroModel

class HeroesPagedListAdapter internal constructor(private val fragment: HeroesFragment) : PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = getItem(position)
        if (hero != null) {
            holder.bindTo(hero, fragment)
        } else {
            holder.clear()
        }
    }

    class ViewHolder(var binding: ItemListHeroBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: HeroModel, fragment: HeroesFragment) {
            binding.hero = hero
            binding.fragment = fragment
            binding.executePendingBindings()
        }

        fun clear() {
            binding.iv.setImageResource(R.drawable.hero_placeholder)
            binding.tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListHeroBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<HeroModel>() {
    override fun areItemsTheSame(oldItem: HeroModel, newItem: HeroModel) = oldItem.codeName == newItem.codeName

    override fun areContentsTheSame(oldItem: HeroModel, newItem: HeroModel) = true
}