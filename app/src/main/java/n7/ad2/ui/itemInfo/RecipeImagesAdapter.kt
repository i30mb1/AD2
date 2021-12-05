package n7.ad2.ui.itemInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemRecipeBinding
import n7.ad2.ui.itemInfo.domain.vo.VORecipe

class RecipeImagesAdapter : ListAdapter<VORecipe, RecipeImagesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemViewType(position: Int): Int = R.layout.item_recipe

    class ViewHolder private constructor(
        private val binding: ItemRecipeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: VORecipe) {
//            binding.recipe = recipe
//            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipeBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VORecipe>() {
        override fun areItemsTheSame(oldItem: VORecipe, newItem: VORecipe): Boolean = oldItem.name == oldItem.name
        override fun areContentsTheSame(oldItem: VORecipe, newItem: VORecipe): Boolean = true
    }

}