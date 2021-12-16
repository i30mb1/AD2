package n7.ad2.hero_page.internal.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.hero_page.databinding.ItemHeroSpellsBinding
import n7.ad2.hero_page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero_page.internal.info.domain.vo.VOSpell

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
            onSpellClickListener: (spell: VOSpell) -> Unit,
        ): HeroSpellsViewHolder {
            val binding = ItemHeroSpellsBinding.inflate(layoutInflater, parent, false)
            val linearLayoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            val spellsListAdapter = SpellsListAdapter(layoutInflater, onSpellClickListener)
            spellsListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            binding.rv.adapter = spellsListAdapter
            binding.rv.layoutManager = linearLayoutManager
            binding.rv.addItemDecoration(HeroSpellsItemDecorator())
            return HeroSpellsViewHolder(binding, spellsListAdapter)
        }
    }

}