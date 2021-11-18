package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemHeroSpellsBinding
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.utils.ImageLoader

class HeroSpellsViewHolder private constructor(
    private val binding: ItemHeroSpellsBinding,
    private val spellsListAdapter: SpellsListAdapter,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(items: VOHeroInfo.Spells) {
        spellsListAdapter.submitList(items.spells)
    }

    fun clear() = Unit

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            imageLoader: ImageLoader,
            onSpellClickListener: (spell: VOSpell) -> Unit,
        ): HeroSpellsViewHolder {
            val binding = ItemHeroSpellsBinding.inflate(layoutInflater, parent, false)
            val linearLayoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            val spellsListAdapter = SpellsListAdapter(layoutInflater, imageLoader, onSpellClickListener)
            spellsListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            binding.rv.adapter = spellsListAdapter
            binding.rv.layoutManager = linearLayoutManager
            binding.rv.addItemDecoration(HeroSpellsItemDecorator())
            return HeroSpellsViewHolder(binding, spellsListAdapter)
        }
    }

}