package n7.ad2.itempage.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.load
import n7.ad2.feature.item.page.R
import n7.ad2.feature.item.page.databinding.ItemInfoRecipeBinding
import n7.ad2.itempage.internal.domain.vo.VOItemInfo

class InfoRecipeViewHolder private constructor(
    private val binding: ItemInfoRecipeBinding,
    private val recipeAdapter: RecipeAdapter,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VOItemInfo.Recipe) {
        binding.ivItem.load(item.urlItemImage, R.drawable.item_placeholder)
        binding.ivItem.transitionName = item.itemName
        binding.ivSign.isVisible = item.recipes.isNotEmpty()
        recipeAdapter.submitList(item.recipes)
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): InfoRecipeViewHolder {
            val binding = ItemInfoRecipeBinding.inflate(layoutInflater, parent, false)
            val recipeAdapter = RecipeAdapter(layoutInflater)
            recipeAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            binding.rv.layoutManager = LinearLayoutManager(layoutInflater.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rv.adapter = recipeAdapter
            binding.rv.addItemDecoration(RecipeItemDecorator())
            return InfoRecipeViewHolder(binding, recipeAdapter)
        }
    }

}
