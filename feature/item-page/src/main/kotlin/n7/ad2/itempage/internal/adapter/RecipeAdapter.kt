package n7.ad2.itempage.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import n7.ad2.feature.itempage.R
import n7.ad2.itempage.internal.domain.vo.VORecipe

class RecipeAdapter(
    private val layoutInflater: LayoutInflater,
) : ListAdapter<VORecipe, RecipeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder = RecipeViewHolder.from(parent, layoutInflater)

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemViewType(position: Int): Int = R.layout.item_recipe

    private class DiffCallback : DiffUtil.ItemCallback<VORecipe>() {
        override fun areItemsTheSame(oldItem: VORecipe, newItem: VORecipe): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: VORecipe, newItem: VORecipe): Boolean = true
    }

}