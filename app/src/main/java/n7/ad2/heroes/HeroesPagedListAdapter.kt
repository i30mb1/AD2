package n7.ad2.heroes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemListHeroBinding
import n7.ad2.heroes.db.HeroModel

class HeroesPagedListAdapter internal constructor(fragment: HeroesFragment) : PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener {
        fragment.startHeroFragment(it, it.getTag(R.id.ViewHolderObject) as HeroModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = getItem(position)
        if (hero != null) holder.bindTo(hero) else holder.clear()
    }

    class ViewHolder(var binding: ItemListHeroBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: HeroModel) {
            binding.hero = hero
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
                val binding = ItemListHeroBinding.inflate(layoutInflater, parent, false)
                binding.root.setOnClickListener(listener)
                return ViewHolder(binding)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<HeroModel>() {
    override fun areItemsTheSame(oldItem: HeroModel, newItem: HeroModel) = oldItem.codeName == newItem.codeName

    override fun areContentsTheSame(oldItem: HeroModel, newItem: HeroModel) = true
}