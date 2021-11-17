package n7.ad2.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemMenuBinding
import n7.ad2.ui.vo.VOMenu


class MainMenuAdapter(
    private val layoutInflater: LayoutInflater,
    itemListener: (menuItem: VOMenu) -> Unit,
) : ListAdapter<VOMenu, MainMenuAdapter.MenuItemHolder>(DiffCallback()) {

    private val itemListener: (menuItem: VOMenu) -> Unit = { menuItem ->
        itemListener(menuItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuItemHolder.from(layoutInflater, parent, itemListener)

    override fun onBindViewHolder(holder: MenuItemHolder, position: Int) = holder.bind(getItem(position))

    class MenuItemHolder private constructor(
        private val binding: ItemMenuBinding,
        private val itemListener: (menuItem: VOMenu) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val backgroundRipple = ContextCompat.getDrawable(binding.root.context, R.drawable.background_ripple)
//        private val transparent = ContextCompat.getColor(binding.root.context, R.color.transparent)

        fun bind(item: VOMenu) {
            binding.vRedLine.isVisible = item.isSelected
            binding.tvTitle.text = item.title
//            binding.root.foreground = if (item.isEnable) backgroundRipple else transparent
            binding.root.setOnClickListener { itemListener.invoke(item) }
        }

        companion object {
            fun from(
                layoutInflater: LayoutInflater,
                parent: ViewGroup,
                itemListener: (menuItem: VOMenu) -> Unit,
            ): MenuItemHolder {
                val binding = ItemMenuBinding.inflate(layoutInflater, parent, false)
                return MenuItemHolder(binding, itemListener)
            }
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<VOMenu>() {
        override fun areItemsTheSame(oldItem: VOMenu, newItem: VOMenu) = oldItem.type == newItem.type
        override fun areContentsTheSame(oldItem: VOMenu, newItem: VOMenu) = oldItem.isSelected == newItem.isSelected
    }

}