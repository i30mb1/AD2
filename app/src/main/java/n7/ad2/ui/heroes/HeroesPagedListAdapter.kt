package n7.ad2.ui.heroes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.databinding.ItemHeroBinding
import n7.ad2.heroes.db.HeroModel
import n7.ad2.ui.heroes.domain.adapter.toVo

class HeroesPagedListAdapter internal constructor(fragment: HeroesFragment) : PagedListAdapter<LocalHero, HeroesPagedListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener {
        fragment.startHeroFragment(it, it.getTag(R.id.ViewHolderObject) as HeroModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = getItem(position)
        if (hero != null) holder.bindTo(hero) else holder.clear()
    }

    class ViewHolder(var binding: ItemHeroBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: LocalHero) {
            binding.hero = hero.toVo()

            binding.root.setTag(R.id.ViewHolderObject, hero)
            binding.executePendingBindings()
        }

        fun clear() {
            binding.iv.setImageResource(R.drawable.hero_placeholder)
            binding.tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup, listener: View.OnClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeroBinding.inflate(layoutInflater, parent, false)
                binding.root.setOnClickListener(listener)
                return ViewHolder(binding)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<LocalHero>() {
    override fun areItemsTheSame(oldItem: LocalHero, newItem: LocalHero) = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: LocalHero, newItem: LocalHero) = true
}