package n7.ad2.itempage.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.load
import n7.ad2.itempage.R
import n7.ad2.itempage.databinding.ItemRecipeBinding
import n7.ad2.itempage.internal.domain.vo.VORecipe

class RecipeViewHolder private constructor(
    private val binding: ItemRecipeBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(recipe: VORecipe) {
        binding.iv.load(recipe.urlItemImage, R.drawable.item_placeholder)
    }

    companion object {
        fun from(parent: ViewGroup, layoutInflater: LayoutInflater): RecipeViewHolder {
            val binding = ItemRecipeBinding.inflate(layoutInflater, parent, false)
            return RecipeViewHolder(binding)
        }
    }

}