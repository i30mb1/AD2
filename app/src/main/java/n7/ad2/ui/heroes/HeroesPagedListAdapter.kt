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
import n7.ad2.ui.heroes.domain.adapter.toVo

class HeroesPagedListAdapter internal constructor(fragment: HeroesFragment) : PagedListAdapter<LocalHero, HeroesPagedListAdapter.ViewHolder>(DiffCallback()) {

    private val listener = View.OnClickListener {
        fragment.startHeroFragment(
                it.getTag(R.id.ViewHolderModel) as LocalHero,
                it.getTag(R.id.ViewHolderBinding) as ItemHeroBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = getItem(position)
        if (hero != null) holder.bindTo(hero) else holder.clear()
    }

    class ViewHolder(
            private val binding: ItemHeroBinding,
            private val listener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(hero: LocalHero) = binding.apply {
            model = hero.toVo()
            root.setOnClickListener(listener)
            root.setTag(R.id.ViewHolderBinding, binding)
            root.setTag(R.id.ViewHolderModel, hero)
            executePendingBindings()
        }

        fun clear() = binding.apply {
            iv.setImageResource(R.drawable.hero_placeholder)
            tv.text = ""
        }

        companion object {
            fun from(parent: ViewGroup, listener: View.OnClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeroBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, listener)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<LocalHero>() {
        override fun areItemsTheSame(oldItem: LocalHero, newItem: LocalHero) = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: LocalHero, newItem: LocalHero) = true
    }
}

